@echo Starting %*
@if not exist "results" mkdir results
@docker run --rm --user "1000:1000" -v "%cd%/results":"/home/user/results" match-experiments %*

@pause