#!/usr/bin/env python3
"""Convert blockbench/kart.bbmodel into KartModelLayer.java."""

from pathlib import Path
import json
import math

ROOT = Path(__file__).resolve().parents[1]
INPUT = ROOT / "blockbench" / "kart.bbmodel"
OUTPUT = ROOT / "src/main/java/com/ruinedmango/kart/client/KartModelLayer.java"
Y_OFFSET = 4.0


def f(value):
    return f"{float(value):.2f}F"


def pose(origin_java, rotation=None):
    x, y, z = origin_java
    if rotation and any(abs(float(v)) > 0.0001 for v in rotation):
        rx, ry, rz = (math.radians(float(v)) for v in rotation)
        return f"PartPose.offsetAndRotation({f(x)}, {f(y)}, {f(z)}, {f(rx)}, {f(ry)}, {f(rz)})"
    return f"PartPose.offset({f(x)}, {f(y)}, {f(z)})"


def cube_uv(cube):
    faces = cube.get("faces", {})
    us = [face["uv"][0] for face in faces.values() if "uv" in face]
    vs = [face["uv"][1] for face in faces.values() if "uv" in face]
    return int(min(us)) if us else 0, int(min(vs)) if vs else 0


def cube_code(cube, origin):
    a = cube["from"]
    b = cube["to"]
    ox, oy, oz = origin
    x0, x1 = sorted((float(a[0]) - ox, float(b[0]) - ox))
    y0, y1 = sorted((oy - float(a[1]), oy - float(b[1])))
    z0, z1 = sorted((float(a[2]) - oz, float(b[2]) - oz))
    u, v = cube_uv(cube)
    return (
        f".texOffs({u}, {v}).addBox("
        f"{f(x0)}, {f(y0)}, {f(z0)}, "
        f"{f(x1 - x0)}, {f(y1 - y0)}, {f(z1 - z0)})"
    )


def parse_outliner(node, elements):
    if isinstance(node, str):
        return elements[node], []
    if "children" not in node:
        return None, []
    cubes = []
    children = []
    for child in node["children"]:
        if isinstance(child, str):
            cubes.append(elements[child])
        else:
            group, sub, nested = parse_outliner(child, elements)
            if group is not None:
                children.append(group)
    node["cubes"] = cubes
    node["children"] = children
    return node, cubes, children


def group_def(group, indent, parent, parent_origin):
    name = group["name"]
    var = name.replace(" ", "_")
    abs_origin = group.get("origin", [0, Y_OFFSET, 0])
    pose_origin = [
        float(abs_origin[0]) - float(parent_origin[0]),
        float(parent_origin[1]) - float(abs_origin[1]),
        float(abs_origin[2]) - float(parent_origin[2]),
    ]
    lines = []
    builder = "CubeListBuilder.create()"
    if group["cubes"]:
        builder = "CubeListBuilder.create()\n" + "\n".join(
            "\t" * (indent + 1) + cube_code(cube, abs_origin) for cube in group["cubes"]
        )
    call = "root.addOrReplaceChild" if parent == "root" else f"{parent}.addOrReplaceChild"
    lines.append(
        f"{'\t' * indent}PartDefinition {var} = {call}(\"{name}\", {builder}, "
        f"{pose(pose_origin, group.get('rotation'))});"
    )
    for child_group in group["children"]:
        lines.extend(group_def(child_group, indent + 1, var, abs_origin))
    return lines


def main():
    model = json.loads(INPUT.read_text())
    elements = {element["uuid"]: element for element in model["elements"]}
    roots = []
    for node in model["outliner"]:
        if isinstance(node, str):
            continue
        group, cubes, children = parse_outliner(node, elements)
        if group is not None:
            roots.append(group)

    lines = []
    for group in roots:
        lines.extend(group_def(group, 2, "root", [0.0, Y_OFFSET, 0.0]))

    source = f"""package com.ruinedmango.kart.client;

import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;

public final class KartModelLayer {{
\tprivate KartModelLayer() {{
\t}}

\tpublic static LayerDefinition createBodyLayer() {{
\t\tMeshDefinition mesh = new MeshDefinition();
\t\tPartDefinition root = mesh.getRoot();
{chr(10).join(lines)}
\t\treturn LayerDefinition.create(mesh, {model['resolution']['width']}, {model['resolution']['height']});
\t}}
}}
"""
    OUTPUT.write_text(source)
    print(f"wrote {OUTPUT}")


if __name__ == "__main__":
    main()
