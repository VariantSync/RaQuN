@echo Starting %*

@docker run --rm -v "%cd%/results":"/home/user/results" match-experiments %*

@pause