# Polices

- **Coolvetica** (logo) : déposer ici `coolvetica.woff2` (et/ou `coolvetica.ttf`).
  La police n'est pas sur Google Fonts, il faut donc le fichier en local.
  Le `@font-face` de `style.css` pointe sur `fonts/coolvetica.woff2`.
  Tant que le fichier est absent, le logo retombe sur Roboto.
  Conversion TTF -> WOFF2 : https://transfonter.org

- **Roboto** (reste de l'interface) : chargée depuis Google Fonts dans
  `index.html`. Pour un rendu hors-ligne, télécharger les graisses 400/500/700,
  les poser ici et remplacer le `<link>` par des `@font-face` locaux.
