# 1. Se déplacer dans le bon dossier
cd docker

# 2. Construire l'image
docker build -t projetgl_tests .

# 3. Taguer l'image (Peu importe le dossier, mais autant rester là)
docker tag projetgl_tests gitlab.ensimag.fr:5050/gl2026/gr5/gl25/decac-image:latest

# 4. Pousser l'image
docker push gitlab.ensimag.fr:5050/gl2026/gr5/gl25/decac-image:latest