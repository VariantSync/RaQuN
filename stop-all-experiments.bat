@echo "Stopping all running experiments (and other docker containers)"
FOR /f "tokens=*" %%i IN ('docker ps -q') DO docker stop %%i