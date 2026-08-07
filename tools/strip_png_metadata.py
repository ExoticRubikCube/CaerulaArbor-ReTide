from __future__ import annotations

import concurrent.futures
import importlib
import os
import subprocess
import sys
from pathlib import Path
from typing import Iterable

PROJECT_ROOT = Path(__file__).resolve().parents[1]
ASSETS_ROOT = PROJECT_ROOT / "src" / "main" / "resources" / "assets"
MIN_PARALLEL_FILE_COUNT = 32
MAX_WORKERS = 8


def ensure_pillow():
    try:
        image_module = importlib.import_module("PIL.Image")
        image_chops = importlib.import_module("PIL.ImageChops")
    except ModuleNotFoundError:
        print("Dependency missing: Pillow")
        print("Installing Pillow via pip...")
        subprocess.check_call([sys.executable, "-m", "pip", "install", "Pillow"])
        image_module = importlib.import_module("PIL.Image")
        image_chops = importlib.import_module("PIL.ImageChops")
        print("Pillow installation complete.")
    return image_module, image_chops


def iter_png_paths(assets_root: Path) -> Iterable[Path]:
    for modid_dir in assets_root.iterdir():
        textures_dir = modid_dir / "textures"
        if textures_dir.is_dir():
            yield from textures_dir.rglob("*.png")


def clean_and_replace(path: Path, image_module, image_chops) -> None:
    with image_module.open(path) as img:
        img.load()
        if img.mode != "RGBA":
            img = img.convert("RGBA")
        r, g, b, a = img.split()
        mask = a.point(lambda p: 255 if p > 0 else 0)
        r = image_chops.multiply(r, mask)
        g = image_chops.multiply(g, mask)
        b = image_chops.multiply(b, mask)
        clean = image_module.merge("RGBA", (r, g, b, a))
        clean.save(path, format="PNG")


def print_relative_paths(title: str, paths: list[Path]) -> None:
    print(f"{title}: {len(paths)}")
    for path in paths:
        print(path.relative_to(PROJECT_ROOT))


def process_png_path(path: Path, image_module, image_chops) -> tuple[str, Path]:
    try:
        clean_and_replace(path, image_module, image_chops)
    except OSError:
        return "error", path
    return "processed", path


def get_worker_count(file_count: int) -> int:
    if file_count < MIN_PARALLEL_FILE_COUNT:
        return 1
    cpu_count = os.cpu_count() or 1
    return min(MAX_WORKERS, file_count, max(2, cpu_count))


def main() -> int:
    image_module, image_chops = ensure_pillow()

    if not ASSETS_ROOT.exists():
        print(f"Assets directory not found: {ASSETS_ROOT}")
        return 1

    png_paths = sorted(iter_png_paths(ASSETS_ROOT))
    scanned = len(png_paths)
    processed = 0
    skipped_errors: list[Path] = []
    worker_count = get_worker_count(scanned)

    if worker_count == 1:
        results = (process_png_path(png_path, image_module, image_chops) for png_path in png_paths)
    else:
        with concurrent.futures.ThreadPoolExecutor(max_workers=worker_count) as executor:
            results = executor.map(lambda png_path: process_png_path(png_path, image_module, image_chops), png_paths)

    for status, png_path in results:
        if status == "processed":
            processed += 1
        elif status == "error":
            skipped_errors.append(png_path)

    print(f"Scanned: {scanned}")
    print(f"Workers used: {worker_count}")
    print(f"Processed: {processed}")
    print_relative_paths("Skipped read errors", skipped_errors)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())