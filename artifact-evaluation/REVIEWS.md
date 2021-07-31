# Reviews - Artifact Evaluation
We want to thank the reviewers for their helpful and valuable feedback. We will try to include as many suggestions as possible
in the camera-ready submission. More complicated changes might be done in the future. 

## Review 1
* (FUTURE WORK) Your Dockerfile is built on top of ubuntu:20.04, but that is a pretty hefty Docker image. I wonder if you could base it on something lighter, like buster-slim, or even Alpine?
  * > This is a good idea and should be done for this artifact and other artifacts which we create. However, it is out of scope for the camera-ready release.
* (DONE) The build-docker-image.sh script doesn't test whether you have rights to access the Docker daemon, and the README does not make clear if it should run with root privileges or not. It appears this requires a user that can use `docker` without resorting to root. I had to add my user to the Docker group, log out and back in:
  `sudo usermod -a -G docker $(id -u)`
  I think it may be good to clarify this in the instructions, or test for it in the script and warn the user.
  * > We implemented the fix-perms solution shown in [1]
* (DONE) In addition, the Docker image that you are building is not redistributable, as it is tied to the UID of the user building it. Rather than that, I would suggest using the "fix-perms" approach mentioned in the "Tips and Tricks of the Docker Captains" talk [1].
  * > This is a really cool suggestion, as we experienced difficulties when trying to get the permissions right. We implemented the solution proposed in the talk.
* (DONE) I would make a further suggestion: run the Maven build in a different Docker stage, and then copy over the generated JAR to a minimal stage. You could further reduce the size of the Docker image that way [2].
* (DONE) The artifact includes a validation option for the scripts, which takes a shorter time and helps checking that it is functional in less time. This already took about 1.5h, so I assume that a full run can take a good while!
  * > We reduced the validation duration by limiting the execution to specific subset ids.
* (DONE) One note - another reviewer mentioned that a few additional imports were needed in order to use RaQuN as a library. Please touch up your documentation for your final version of the artifact.
  * > We updated the README
    
[1]: https://youtu.be/woBI466WMR8?t=2313
[2]: https://docs.docker.com/develop/develop-images/multistage-build/


## Review 2
* (DONE) The sample Java codes in README.md were able to be compiled and run after inserting the following two more import statements for the first one (Matching a Dataset Stored in CSV Format):
  * `import java.util.Set;`
  * `import de.variantsync.matching.raqun.data.RMatch;`
  * > We updated the README
* (DONE) the Java heap memory setting was not enough. -Xmx was required. 16G looks enough.
  * > We had not considered that the default maximum heap space is calculated based on a system's capacity. We added the -Xmx flag to all calls.
* (DONE) amendment on script for Linux: group id had to be set as new one, since an error
  * > We implemented the fix-perms solution shown in [1]
* (DONE) Also, for RQ3, the indicated time was still too long even for single instance as suggested, for usual evaluation process, so smaller size of dataset had to be used. An amendment was necessary to the parameter file and the Java source code.
  * > We fixed the argument parsing in AbstractRQRunner that caused a problem. We reduced the validation duration by limiting the execution to specific subset ids.
* (DOING) As a side note, Python script evaluation.py runs outside of the Docker environment, except for validation, so the user is responsible to make sure the Python environment is set properly (it does not happen for validation task since it runs under Docker environment).
For example, when the tkinter environment is not set properly, it does not plot the graph for experiments other than the validation package.
A warning would be quite helpful as there was no error message with that situation.
