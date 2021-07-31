@echo "Stopping all running experiments. This will take a moment..."
FOR /f "tokens=*" %%i IN ('docker ps -a -q --filter "ancestor=match-experiments"') DO docker stop %%i
@echo "...done."