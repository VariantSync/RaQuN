@echo Starting extraction for %*

@docker run --rm --user "1000:1000" -v "%cd%/results/results":"/home/user/results" match-experiments %*

@pause