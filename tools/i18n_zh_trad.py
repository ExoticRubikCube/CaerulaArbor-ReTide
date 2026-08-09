from __future__ import annotations

import json
import subprocess
import sys
from pathlib import Path

PROJECT_ROOT = Path(__file__).resolve().parents[1]
LANG_DIR = PROJECT_ROOT / "src" / "main" / "resources" / "assets" / "caerula_arbor" / "lang"
SOURCE_FILE = LANG_DIR / "zh_cn.json"

TARGETS: list[tuple[str, str, str | None]] = [
    # (输出文件名, 首选OpenCC配置, 回退配置)
    ("zh_tw.json", "s2twp", None),   # 台湾繁体（含词汇修正：伺服器、程式碼、記憶體）
    ("zh_hk.json", "s2hkp", "s2twp"),# 香港繁体（opencc-python-reimplemented 缺省时回退 s2twp）
    ("zh_mo.json", "s2twp", None),   # 澳门繁体（与台湾术语差异极小，复用 s2twp）
]


def ensure_opencc():
    try:
        return __import__("opencc")
    except ImportError:
        print("[依赖] 未安装 opencc-python-reimplemented，正在安装...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", "opencc-python-reimplemented"])
        print("[依赖] 安装完成。")
        return __import__("opencc")


def convert_dict(obj, converter):
    if isinstance(obj, dict):
        return {k: convert_dict(v, converter) for k, v in obj.items()}
    elif isinstance(obj, list):
        return [convert_dict(item, converter) for item in obj]
    elif isinstance(obj, str):
        return converter.convert(obj)
    else:
        return obj


def write_json_no_trailing_newline(file_path: Path, data) -> None:
    text = json.dumps(data, ensure_ascii=False, indent=2).rstrip("\r\n")
    file_path.write_bytes(text.encode("utf-8"))


def main() -> int:
    if not SOURCE_FILE.is_file():
        print(f"[错误] 找不到源文件：{SOURCE_FILE}")
        return 2

    opencc = ensure_opencc()

    print(f"[信息] 源文件：{SOURCE_FILE}")
    source_data = json.loads(SOURCE_FILE.read_text(encoding="utf-8-sig"))
    print(f"[信息] 加载 {len(source_data)} 条语言条目")

    for filename, primary_cfg, fallback_cfg in TARGETS:
        out_path = LANG_DIR / filename
        used_cfg: str | None = None
        converter = None
        last_err: Exception | None = None
        for cfg in (primary_cfg, fallback_cfg):
            if cfg is None:
                continue
            try:
                converter = opencc.OpenCC(cfg)
                used_cfg = cfg
                break
            except Exception as e:
                last_err = e
                continue
        if converter is None:
            print(f"[跳过] {filename}: 所有配置加载失败 ({last_err})")
            continue

        result = convert_dict(source_data, converter)
        write_json_no_trailing_newline(out_path, result)

        note = "" if used_cfg == primary_cfg else f" (fallback: {primary_cfg}->{used_cfg})"
        print(f"[完成] {filename:12s} ({used_cfg}){note} -> {len(result)} 条")

    print("\n全部完成！")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
