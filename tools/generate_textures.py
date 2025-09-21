from PIL import Image, ImageDraw
import os

ITEMS = {
    "thingy": {"type": "sphere", "color": "#FFF064"},
    "thingy_good": {"type": "sphere", "color": "#1DB1FF"},
    "thingy_super": {"type": "sphere", "color": "#C500CC"},
    "the_stick": {"type": "rod", "color": "#B37A42"},
    "the_stick_good": {"type": "rod", "color": "#1DB1FF"},
    "the_stick_super": {"type": "rod", "color": "#C500CC"},
    "magic_apple": {"type": "apple", "color": "#48C774"},
    "magic_good_apple": {"type": "apple", "color": "#F1C40F"},
    "magic_super_apple": {"type": "apple", "color": "#C500CC"},
    "magic_cookie": {"type": "cookie", "color": "#A47539"},
    "magic_good_cookie": {"type": "cookie", "color": "#F6C28B"},
    "magic_super_cookie": {"type": "cookie", "color": "#E67EFA"},
    "magick_pedia": {"type": "book", "color": "#5C2D91", "accent": "#E6D5FF"},
    "magick_tablet": {"type": "tablet", "color": "#E4D3A1", "accent": "#9F8B4D"},
    "mat_resistance": {"type": "ingot", "color": "#9BC5FF", "accent": "#4E79D4"},
    "essence_arcane": {"type": "gem", "color": "#FF0000"},
    "essence_cold": {"type": "gem", "color": "#FFFFFF"},
    "essence_earth": {"type": "gem", "color": "#382713"},
    "essence_fire": {"type": "gem", "color": "#FF4B00"},
    "essence_ice": {"type": "gem", "color": "#90FFFF"},
    "essence_life": {"type": "gem", "color": "#00FF00"},
    "essence_lightning": {"type": "gem", "color": "#FF54FD"},
    "essence_shield": {"type": "gem", "color": "#FFF638"},
    "essence_steam": {"type": "gem", "color": "#ABABAB"},
    "essence_water": {"type": "gem", "color": "#2529FF"},
    "staff": {"type": "staff", "handle": "#70492C", "accent": "#B08D57"},
    "staff_grand": {"type": "staff", "handle": "#70492C", "accent": "#F9D65C"},
    "staff_super": {"type": "staff", "handle": "#70492C", "accent": "#C500CC"},
    "hemmy_staff": {"type": "staff", "handle": "#70492C", "accent": "#00E5D2"},
    "staff_blessing": {"type": "staff", "handle": "#70492C", "accent": "#8BFF6C"},
    "staff_destruction": {"type": "staff", "handle": "#70492C", "accent": "#FF512F"},
    "staff_telekinesis": {"type": "staff", "handle": "#70492C", "accent": "#5EC5FF"},
    "staff_manipulation": {"type": "staff", "handle": "#70492C", "accent": "#FF9F43"},
    "hat": {"type": "hat", "color": "#5B3A1B", "band": "#E1C697"},
    "hat_of_immunity": {"type": "hat", "color": "#F5C542", "band": "#FFF0B3"},
    "hat_of_risk": {"type": "hat", "color": "#C0392B", "band": "#F5B7B1"},
    "hat_of_resistance": {"type": "hat", "color": "#34495E", "band": "#85C1E9"},
    "test": {"type": "star", "color": "#B0B0B0"}
}

OUTPUT_DIR = os.path.join("src", "main", "resources", "assets", "minegicka", "textures", "item")
os.makedirs(OUTPUT_DIR, exist_ok=True)


def hex_to_rgba(hex_color, alpha=255):
    hex_color = hex_color.lstrip('#')
    r = int(hex_color[0:2], 16)
    g = int(hex_color[2:4], 16)
    b = int(hex_color[4:6], 16)
    return (r, g, b, alpha)


def lighten(color, factor):
    r, g, b, a = color
    r = min(255, int(r + (255 - r) * factor))
    g = min(255, int(g + (255 - g) * factor))
    b = min(255, int(b + (255 - b) * factor))
    return (r, g, b, a)


def darken(color, factor):
    r, g, b, a = color
    r = max(0, int(r * (1 - factor)))
    g = max(0, int(g * (1 - factor)))
    b = max(0, int(b * (1 - factor)))
    return (r, g, b, a)


def draw_sphere(draw, color):
    draw.ellipse((6, 6, 26, 26), fill=color, outline=lighten(color, 0.4))
    draw.ellipse((10, 10, 22, 20), fill=lighten(color, 0.6))


def draw_rod(draw, color):
    base = hex_to_rgba("#3E1F06")
    draw.rectangle((13, 5, 19, 27), fill=base)
    draw.rectangle((14, 6, 18, 26), fill=darken(base, 0.2))
    draw.rectangle((15, 7, 17, 25), fill=color)


def draw_apple(draw, color):
    c = color
    draw.ellipse((6, 8, 26, 26), fill=c, outline=darken(c, 0.3))
    draw.ellipse((12, 6, 18, 14), fill=darken(c, 0.2))
    leaf_color = hex_to_rgba("#3FA34D")
    draw.polygon([(16, 8), (20, 4), (22, 10)], fill=leaf_color)
    draw.line((16, 7, 16, 3), fill=darken(c, 0.4), width=2)
    draw.ellipse((10, 13, 18, 19), fill=lighten(c, 0.6))


def draw_cookie(draw, color):
    draw.ellipse((6, 8, 26, 26), fill=color, outline=darken(color, 0.3))
    chips = [(11, 13), (19, 12), (15, 20), (21, 19), (11, 21)]
    chip_color = darken(color, 0.5)
    for x, y in chips:
        draw.ellipse((x, y, x + 3, y + 3), fill=chip_color)


def draw_book(draw, color, accent):
    draw.rectangle((7, 6, 25, 26), fill=color, outline=darken(color, 0.4))
    draw.rectangle((9, 8, 12, 26), fill=darken(color, 0.2))
    draw.rectangle((13, 10, 23, 24), fill=accent)
    draw.line((13, 13, 23, 13), fill=darken(accent, 0.3), width=1)
    draw.line((13, 18, 23, 18), fill=darken(accent, 0.3), width=1)


def draw_tablet(draw, color, accent):
    draw.rounded_rectangle((6, 6, 26, 26), radius=4, fill=color, outline=darken(color, 0.4))
    draw.line((10, 12, 22, 12), fill=accent, width=2)
    draw.line((10, 16, 22, 16), fill=accent, width=2)
    draw.line((10, 20, 18, 20), fill=accent, width=2)


def draw_ingot(draw, color, accent):
    draw.polygon([(8, 18), (12, 8), (20, 8), (24, 18), (20, 24), (12, 24)], fill=color, outline=darken(color, 0.3))
    draw.polygon([(10, 17), (13, 11), (19, 11), (22, 17), (19, 21), (13, 21)], fill=lighten(color, 0.3))
    draw.line((10, 17, 22, 17), fill=accent, width=1)


def draw_gem(draw, color):
    draw.polygon([(16, 5), (25, 16), (16, 27), (7, 16)], fill=color, outline=darken(color, 0.3))
    draw.polygon([(16, 7), (23, 16), (16, 25), (9, 16)], fill=lighten(color, 0.4))


def draw_staff(draw, handle_color, accent_color):
    draw.rectangle((14, 6, 18, 26), fill=handle_color)
    draw.rectangle((15, 7, 17, 25), fill=darken(handle_color, 0.2))
    draw.ellipse((11, 1, 21, 11), fill=accent_color, outline=lighten(accent_color, 0.4))
    draw.ellipse((13, 3, 19, 9), fill=lighten(accent_color, 0.5))
    draw.rectangle((13, 26, 19, 28), fill=darken(handle_color, 0.3))


def draw_hat(draw, color, band_color):
    draw.polygon([(6, 20), (26, 20), (16, 6)], fill=color, outline=darken(color, 0.3))
    draw.rectangle((4, 20, 28, 24), fill=darken(color, 0.4))
    draw.rectangle((10, 16, 22, 19), fill=band_color)


def draw_star(draw, color):
    points = [(16, 4), (19, 13), (28, 13), (21, 19), (24, 28), (16, 22), (8, 28), (11, 19), (4, 13), (13, 13)]
    draw.polygon(points, fill=color, outline=darken(color, 0.4))


def make_texture(item_id, spec):
    img = Image.new("RGBA", (32, 32), (0, 0, 0, 0))
    draw = ImageDraw.Draw(img)
    t = spec["type"]
    if t == "sphere":
        draw_sphere(draw, hex_to_rgba(spec["color"]))
    elif t == "rod":
        draw_rod(draw, hex_to_rgba(spec["color"]))
    elif t == "apple":
        draw_apple(draw, hex_to_rgba(spec["color"]))
    elif t == "cookie":
        draw_cookie(draw, hex_to_rgba(spec["color"]))
    elif t == "book":
        draw_book(draw, hex_to_rgba(spec["color"]), hex_to_rgba(spec["accent"]))
    elif t == "tablet":
        draw_tablet(draw, hex_to_rgba(spec["color"]), hex_to_rgba(spec["accent"]))
    elif t == "ingot":
        draw_ingot(draw, hex_to_rgba(spec["color"]), hex_to_rgba(spec["accent"]))
    elif t == "gem":
        draw_gem(draw, hex_to_rgba(spec["color"]))
    elif t == "staff":
        draw_staff(draw, hex_to_rgba(spec["handle"]), hex_to_rgba(spec["accent"]))
    elif t == "hat":
        draw_hat(draw, hex_to_rgba(spec["color"]), hex_to_rgba(spec["band"]))
    elif t == "star":
        draw_star(draw, hex_to_rgba(spec["color"]))
    else:
        draw_sphere(draw, hex_to_rgba(spec.get("color", "#FFFFFF")))
    output_path = os.path.join(OUTPUT_DIR, f"{item_id}.png")
    img.save(output_path)

for item_id, spec in ITEMS.items():
    make_texture(item_id, spec)

print(f"Generated {len(ITEMS)} textures in {OUTPUT_DIR}")
