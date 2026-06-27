from __future__ import annotations

import concurrent.futures
import importlib
import os
import subprocess
import sys
from pathlib import Path
from typing import Iterable

PNG_METADATA_CHUNKS = {
    b"cHRM",
    b"eXIf",
    b"gAMA",
    b"iCCP",
    b"iTXt",
    b"pHYs",
    b"sRGB",
    b"tEXt",
    b"tIME",
    b"zTXt",
}

PROJECT_ROOT = Path(__file__).resolve().parents[1]
TEXTURE_ROOT = PROJECT_ROOT / "src" / "main" / "resources" / "assets" / "caerula_arbor" / "textures"
PNG_SIGNATURE = b"\x89PNG\r\n\x1a\n"
IEND_CHUNK = b"IEND"
MIN_PARALLEL_FILE_COUNT = 32
MAX_WORKERS = 8


def ensure_pillow():
    try:
        image_module = importlib.import_module("PIL.Image")
    except ModuleNotFoundError:
        print("Dependency missing: Pillow")
        print("Installing Pillow via pip...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", "Pillow"])
        image_module = importlib.import_module("PIL.Image")
        print("Pillow installation complete.")
    return image_module


def iter_png_paths(root: Path) -> Iterable[Path]:
    yield from root.rglob("*.png")


def png_has_metadata(path: Path) -> bool | None:
    with path.open("rb") as file:
        if file.read(8) != PNG_SIGNATURE:
            return None

        while True:
            length_bytes = file.read(4)
            if len(length_bytes) < 4:
                return False

            chunk_type_bytes = file.read(4)
            if len(chunk_type_bytes) < 4:
                return False

            length = int.from_bytes(length_bytes, "big")
            if chunk_type_bytes in PNG_METADATA_CHUNKS:
                return True

            file.seek(length + 4, 1)
            if chunk_type_bytes == IEND_CHUNK:
                return False


def rewrite_without_metadata(path: Path, image_module) -> None:
    with image_module.open(path) as image:
        image.load()
        clean = image_module.frombytes(image.mode, image.size, image.tobytes())

        if image.mode == "P":
            palette = image.getpalette()
            if palette is not None:
                clean.putpalette(palette)
            if "transparency" in image.info:
                clean.info["transparency"] = image.info["transparency"]
        clean.save(path, format="PNG")


def print_relative_paths(title: str, paths: list[Path]) -> None:
    print(f"{title}: {len(paths)}")
    for path in paths:
        print(path.relative_to(PROJECT_ROOT))


def process_png_path(path: Path, image_module) -> tuple[str, Path]:
    try:
        has_metadata = png_has_metadata(path)
    except OSError:
        return "error", path

    if has_metadata is None:
        return "non_png", path
    if not has_metadata:
        return "no_metadata", path

    try:
        rewrite_without_metadata(path, image_module)
    except OSError:
        return "error", path
    return "processed", path


def get_worker_count(file_count: int) -> int:
    if file_count < MIN_PARALLEL_FILE_COUNT:
        return 1

    cpu_count = os.cpu_count() or 1
    return min(MAX_WORKERS, file_count, max(2, cpu_count))


def main() -> int:
    image_module = ensure_pillow()

    if not TEXTURE_ROOT.exists():
        print(f"Texture directory not found: {TEXTURE_ROOT}")
        return 1

    png_paths = sorted(iter_png_paths(TEXTURE_ROOT))
    scanned = len(png_paths)
    processed: list[Path] = []
    skipped_non_png: list[Path] = []
    skipped_without_metadata = 0
    skipped_errors: list[Path] = []
    worker_count = get_worker_count(scanned)

    if worker_count == 1:
        results = (process_png_path(png_path, image_module) for png_path in png_paths)
    else:
        with concurrent.futures.ThreadPoolExecutor(max_workers=worker_count) as executor:
            results = executor.map(lambda png_path: process_png_path(png_path, image_module), png_paths)

    for status, png_path in results:
        if status == "processed":
            processed.append(png_path)
            continue
        if status == "non_png":
            skipped_non_png.append(png_path)
            continue
        if status == "error":
            skipped_errors.append(png_path)
            continue
        skipped_without_metadata += 1

    print(f"Scanned: {scanned}")
    print(f"Workers used: {worker_count}")
    print_relative_paths("Processed", processed)
    print(f"Skipped without metadata: {skipped_without_metadata}")
    print_relative_paths("Skipped non-PNG signature files", skipped_non_png)
    print_relative_paths("Skipped read errors", skipped_errors)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
