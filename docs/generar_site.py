
from pathlib import Path
from html import escape
import shutil
import markdown

DOCS = Path(__file__).resolve().parent
SITE = DOCS / "site"


def crear_pagina(titulo, contenido):
    return f"""<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>{escape(titulo)}</title>
    <style>
        body {{
            font-family: system-ui, sans-serif;
            max-width: 960px;
            margin: 40px auto;
            padding: 0 20px;
            line-height: 1.7;
        }}
        nav {{ margin-bottom: 30px; }}
        img {{ max-width: 100%; height: auto; }}
        pre {{
            background: #f4f4f4;
            padding: 16px;
            overflow-x: auto;
            border-radius: 6px;
        }}
        table {{ border-collapse: collapse; width: 100%; }}
        th, td {{ border: 1px solid #ccc; padding: 10px; }}
        a {{ color: #0969da; }}
    </style>
</head>
<body>
    <nav><a href="index.html">Volver al índice</a></nav>
    <main>{contenido}</main>
</body>
</html>"""


def generar_site():
    archivos = sorted(DOCS.glob("*.md"))

    if not archivos:
        raise SystemExit("No se encontraron archivos Markdown en docs/")

    if SITE.exists():
        shutil.rmtree(SITE)

    SITE.mkdir(parents=True)

    enlaces = []

    for archivo in archivos:
        titulo = archivo.stem.replace("boletin", "Boletín ")
        contenido = archivo.read_text(encoding="utf-8")

        html = markdown.markdown(
            contenido,
            extensions=["tables", "fenced_code", "toc"]
        )

        destino = SITE / f"{archivo.stem}.html"
        destino.write_text(
            crear_pagina(titulo, html),
            encoding="utf-8"
        )

        enlaces.append(
            f'<li><a href="{escape(destino.name)}">'
            f'{escape(titulo)}</a></li>'
        )

        print(f"Generado: {destino.name}")

    # Copiar imágenes conservando la estructura de carpetas
    imagenes = DOCS / "imagenes"

    if imagenes.exists():
        shutil.copytree(imagenes, SITE / "imagenes")

    # Generar la página principal
    indice = "<h1>Prácticas Continuas</h1>"
    indice += "<h2>Memorias de los boletines</h2>"
    indice += "<ul>" + "\n".join(enlaces) + "</ul>"

    (SITE / "index.html").write_text(
        crear_pagina("Índice de boletines", indice),
        encoding="utf-8"
    )

    print("Sitio generado correctamente en docs/site/")


if __name__ == "__main__":
    generar_site()
