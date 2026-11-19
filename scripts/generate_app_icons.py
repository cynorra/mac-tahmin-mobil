import os
from PIL import Image, ImageDraw

import sys

DEFAULT_ICON = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "ic_launcher-playstore.png")
SRC_PATH = sys.argv[1] if len(sys.argv) > 1 else os.environ.get("ICON_SRC", DEFAULT_ICON)
RES_DIR = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main", "res")

# Target densities and sizes for Android launcher icons
DENSITIES = {
    "mipmap-mdpi": {"legacy": 48, "foreground": 108},
    "mipmap-hdpi": {"legacy": 72, "foreground": 162},
    "mipmap-xhdpi": {"legacy": 96, "foreground": 216},
    "mipmap-xxhdpi": {"legacy": 144, "foreground": 324},
    "mipmap-xxxhdpi": {"legacy": 192, "foreground": 432},
}

def create_round_icon(img):
    size = img.size[0]
    mask = Image.new("L", (size, size), 0)
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, size - 1, size - 1), fill=255)
    
    round_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    round_img.paste(img, (0, 0), mask=mask)
    return round_img

def create_squircle_icon(img, radius_ratio=0.22):
    size = img.size[0]
    radius = int(size * radius_ratio)
    mask = Image.new("L", (size, size), 0)
    draw = ImageDraw.Draw(mask)
    draw.rounded_rectangle((0, 0, size - 1, size - 1), radius=radius, fill=255)
    
    squircle_img = Image.new("RGBA", (size, size), (0, 0, 0, 0))
    squircle_img.paste(img, (0, 0), mask=mask)
    return squircle_img

def main():
    if not os.path.exists(SRC_PATH):
        raise FileNotFoundError(f"Source icon not found at {SRC_PATH}")

    src_img = Image.open(SRC_PATH).convert("RGBA")
    print(f"Loaded master icon: {src_img.size}")

    # 1. Generate Google Play Store 512x512 icon
    playstore_dir = os.path.join(os.path.dirname(__file__), "..", "app", "src", "main")
    playstore_path = os.path.join(playstore_dir, "ic_launcher-playstore.png")
    playstore_img = src_img.resize((512, 512), Image.Resampling.LANCZOS)
    playstore_img.save(playstore_path, "PNG")
    print(f"Saved Play Store icon: {playstore_path}")

    # 2. Generate mipmap densities
    for folder, specs in DENSITIES.items():
        out_dir = os.path.join(RES_DIR, folder)
        os.makedirs(out_dir, exist_ok=True)

        legacy_size = specs["legacy"]
        fg_size = specs["foreground"]

        # Legacy squircle icon
        resized_legacy = src_img.resize((legacy_size, legacy_size), Image.Resampling.LANCZOS)
        squircle = create_squircle_icon(resized_legacy)
        squircle.save(os.path.join(out_dir, "ic_launcher.png"), "PNG")

        # Legacy round icon
        round_icon = create_round_icon(resized_legacy)
        round_icon.save(os.path.join(out_dir, "ic_launcher_round.png"), "PNG")

        # Adaptive icon foreground
        resized_fg = src_img.resize((fg_size, fg_size), Image.Resampling.LANCZOS)
        resized_fg.save(os.path.join(out_dir, "ic_launcher_foreground.png"), "PNG")

        print(f"Generated {folder}: ic_launcher ({legacy_size}px), ic_launcher_round ({legacy_size}px), ic_launcher_foreground ({fg_size}px)")

    # 3. Create anydpi-v26 adaptive icon XML definitions
    anydpi_dir = os.path.join(RES_DIR, "mipmap-anydpi-v26")
    os.makedirs(anydpi_dir, exist_ok=True)

    adaptive_xml = '''<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background" />
    <foreground android:drawable="@mipmap/ic_launcher_foreground" />
</adaptive-icon>
'''
    with open(os.path.join(anydpi_dir, "ic_launcher.xml"), "w", encoding="utf-8") as f:
        f.write(adaptive_xml)

    with open(os.path.join(anydpi_dir, "ic_launcher_round.xml"), "w", encoding="utf-8") as f:
        f.write(adaptive_xml)

    print("Adaptive icon XMLs generated in mipmap-anydpi-v26")

if __name__ == "__main__":
    main()
