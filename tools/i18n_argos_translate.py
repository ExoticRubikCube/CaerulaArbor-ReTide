from __future__ import annotations

import json
import logging
import re
import subprocess
import sys
from pathlib import Path

for _log_name in ("stanza", "argostranslate", "ctranslate2"):
    logging.getLogger(_log_name).setLevel(logging.ERROR)

PROJECT_ROOT = Path(__file__).resolve().parents[1]
LANG_DIR = PROJECT_ROOT / "src" / "main" / "resources" / "assets" / "caerula_arbor" / "lang"
SOURCE_FILE = LANG_DIR / "en_us.json"

# Minecraft 内部名(输出文件) | Argos 目标语代码
# 翻译路线固定为：en_us.json（人工维护的英文基准） -> 其他 6 种外语
# 中文家族（zh_cn / zh_tw / zh_hk / zh_mo）由 zh_cn.json 人工基准 + i18n_zh_trad.py 繁简转换负责，
TARGETS: list[tuple[str, str]] = [
    ("ru_ru.json", "ru"), # 俄语         -> en -> ru
    ("ja_jp.json", "ja"), # 日语         -> en -> ja
    ("it_it.json", "it"), # 意大利语     -> en -> it
    ("fr_fr.json", "fr"), # 法语         -> en -> fr
    ("de_de.json", "de"), # 德语         -> en -> de
    ("es_es.json", "es"), # 西班牙语     -> en -> es
]

# ---- 占位符护罩：保护 %1$d / %2$s / %1$d%% / {} 等格式化占位符 ----
TOKEN_PREFIX = "\u25c6"  # ◆
PLACEHOLDER_RE = re.compile(r"(?:%[1-9]\$(?:d|s|f|%)|[{}])")
RESTORE_RE = re.compile(
    r"[\u25c6\u300a\u300b\u300c\u300d\uff3c\uff3e]"
    r"\s*([0-9\uff10-\uff19]+)\s*"
    r"[\u25c6\u300a\u300b\u300c\u300d\uff3c\uff3e]"
)
FULLWIDTH_TABLE = str.maketrans({chr(0xFF10 + i): str(i) for i in range(10)})


def ensure_argos():
    def _import():
        pkg = __import__("argostranslate")
        __import__("argostranslate.package", fromlist=["package"])
        __import__("argostranslate.translate", fromlist=["translate"])
        return pkg
    try:
        return _import()
    except ImportError:
        print("[依赖] 未安装 argostranslate，正在安装...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", "argostranslate"])
        print("[依赖] 安装完成。")
        return _import()


def protect(text: str) -> tuple[str, list[str]]:
    ph: list[str] = []
    def _sub(m: re.Match[str]) -> str:
        ph.append(m.group(0))
        return f"{TOKEN_PREFIX}{len(ph) - 1}{TOKEN_PREFIX}"
    return PLACEHOLDER_RE.sub(_sub, text), ph


def restore(text: str, ph: list[str]) -> str:
    def _sub(m: re.Match[str]) -> str:
        try:
            idx = int(m.group(1).translate(FULLWIDTH_TABLE))
        except ValueError:
            return m.group(0)
        if 0 <= idx < len(ph):
            return ph[idx]
        return m.group(0)
    return RESTORE_RE.sub(_sub, text)


def ensure_language_pair(argos, src: str, dst: str) -> bool:
    """确保 src->dst 语言包已安装；缺包时自动从官方索引下载安装。"""
    package = argos.package

    installed = {
        (ipkg.from_code, ipkg.to_code) for ipkg in package.get_installed_packages()
    }
    if (src, dst) in installed:
        return True
    print(f"[模型] 未检测到 {src}->{dst} 语言包，正在查询官方索引...")
    try:
        package.update_package_index()
    except Exception as e:
        print(f"[警告] 索引更新失败（可能无网络）：{e}")
        return False
    available = package.get_available_packages()
    target = next(
        (p for p in available if p.from_code == src and p.to_code == dst),
        None,
    )
    if target is None:
        print(f"[警告] 官方索引中不存在 {src}->{dst} 语言包")
        return False
    print(f"[模型] 开始下载 {target} （首次运行，大小通常 50-300MB，请耐心等待）")
    try:
        dl_path = Path(target.download())
        package.install_from_path(str(dl_path))
    except Exception as e:
        print(f"[警告] 下载/安装失败：{e}")
        return False
    print(f"[模型] {src}->{dst} 已安装完毕")
    return True


def build_translator(argos, target_lang: str):
    """返回 (translator_callable, 路径标签)；缺直接包时抛 RuntimeError。"""
    translate = argos.translate
    src = "en"
    dst = target_lang

    if ensure_language_pair(argos, src, dst):
        def direct(text: str) -> str:
            return translate.translate(text, src, dst)
        return direct, f"{src}->{dst}"

    raise RuntimeError(f"缺少 en->{dst} 语言包（官方索引亦无，需自行补充）")


def translate_i18n(argos, input_file: Path, output_file: Path, target_lang: str) -> None:
    translator, path_label = build_translator(argos, target_lang)

    # ---- 增量复用：目标文件已存在的 key 直接复用，仅翻译新增 ----
    existing: dict = {}
    if output_file.is_file():
        try:
            existing = json.loads(output_file.read_text(encoding="utf-8-sig"))
        except Exception:
            existing = {}
    reused = 0
    failed: list[str] = []
    translated_count = 0

    def process_dict(obj, key_path: str = ""):
        nonlocal reused, translated_count
        if isinstance(obj, dict):
            return {k: process_dict(v, f"{key_path}.{k}" if key_path else k) for k, v in obj.items()}
        elif isinstance(obj, list):
            return [process_dict(item, f"{key_path}[{i}]") for i, item in enumerate(obj)]
        elif isinstance(obj, str):
            if not obj.strip():
                return obj
            if key_path in existing and isinstance(existing[key_path], str):
                reused += 1
                return existing[key_path]
            protected, ph_list = protect(obj)
            try:
                result: str = translator(protected) or protected
            except Exception:
                failed.append(key_path)
                return obj
            translated = restore(result, ph_list) if ph_list else result
            translated_count += 1
            return translated
        else:
            return obj

    source_data = json.loads(input_file.read_text(encoding="utf-8-sig"))
    result_data = process_dict(source_data)

    text = json.dumps(result_data, ensure_ascii=False, indent=2).rstrip("\r\n")
    output_file.write_bytes(text.encode("utf-8"))

    summary = f"[完成][{path_label:8s}] -> {output_file.name}"
    summary += f"   总{len(result_data)} 新译{translated_count} 复用{reused}"
    if failed:
        summary += f"   失败{len(failed)}（保留原文）"
    print(summary)


def main() -> int:
    if not SOURCE_FILE.is_file():
        print(f"[错误] 源文件不存在：{SOURCE_FILE}")
        print("       请先人工维护好 en_us.json 英文版基准再运行本脚本。")
        return 2

    argos = ensure_argos()

    print(f"[信息] 源文件：{SOURCE_FILE}")
    print(f"[信息] 模式：本地离线 Argos Translate(以英文为源生成所有其他语种)")
    total = len(TARGETS)
    for i, (filename, lang) in enumerate(TARGETS, 1):
        out = LANG_DIR / filename
        print(f"\n[{i}/{total}] 翻译 {filename}  (en -> {lang})")
        try:
            translate_i18n(argos, SOURCE_FILE, out, lang)
        except RuntimeError as e:
            print(f"[跳过] {filename}: {e}")
        except KeyboardInterrupt:
            print(f"\n[中断] 已保存 {filename} 当前进度。")
            return 130

    print("\n全部完成！")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
