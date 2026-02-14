@echo off
echo 🔄 Sincronizando com GitHub...
echo.

cd /d "c:\Users\Naja Info\CascadeProjects\windsurf-project\organizador-financas"

echo 📥 Baixando alterações do repositório remoto...
git pull origin main --allow-unrelated-histories
echo.

echo 📤 Enviando suas alterações...
git push origin main
echo.

echo ✅ Sincronização concluída!
echo 📂 Acesse: https://github.com/andersonconstante/organizador-financas
echo.
pause
