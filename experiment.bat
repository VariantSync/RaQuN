@echo Starting %*
@if not exist "results" mkdir results

@docker run --rm -v "%cd%/results":"/home/user/results" match-experiments %*

@pause