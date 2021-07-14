@echo Starting %*

@docker run --rm --user "1000:1000" -v "%cd%/results":"/home/user/results" match-experiments %*

@pause