# Great-Versioning-Tool
(Simplified git)
The system supports only files. The basic unit of operation is versions. Each version contains:

- version number (from 0 to Integer.MAX_VALUE);
- the message (commit message) that was added when approving (commit) the version;
- all files that were added (add command) to gvt. Files approved in a particular version cannot be modified within that version - approving their modification means creating a new version.
- The last version is the version that was created last. Create a new version can be commands: init (only version 0), add, detach, commit. These commands always work on the last version, not on the one that is currently downloaded.

Launching the application:

The command is always the first parameter of running the program.If there are no parameters, the program prints to System.out: Please specify command., and returns error code 1.If you specify an unknown command, the program prints to System.out: “Unknown command {specifed-command}.”.
All commands (except init) work only in the initialized directory.

init:
void init()

Initializes the gvt system in the current directory, and sets the active and last version to 0. Message to version 0: “GVT initialized”.
- If the directory was already initialized, prints to System.out: “Current directory is already initialized.”
- If any other error occurred, it prints System.out: “Underlying system problem. See ERR for details.” And it prints to System.err stack trace.
- If the initialization was successful, it prints to System.out: “Current directory initialized successfully.”.
On initialization, Gvt should create a directory named .gvt. Inside this directory, the system stores all the data necessary for its operation.

add:
void add(String fileName, String optionalParameter)

Adds to the last version the file indicated as parameters to this command. Has an optional parameter {“Message content in quotes}”, which can be given as the last parameter. This is the user's message. It is docked to the default message; together they will form a version message.
If no file is indicated, it prints to System.out: “Please specify file to add.”
If a file is indicated, it:
- if successful, it prints to System.out: “File added successfully. File: {file-name}” and creates a new version.
- If the current directory is not initialized, it prints to System.out the message: “Current directory is not initialized. Please use “init” command to initialize.”. This error takes precedence over all others.
- If the file does not exist writes to System.out: “File not found. File: {file-name}”.
- if the file is already added, it prints to System.out: “File already added. File: {file-name}”.
- if any other error has occurred, prints System.out: “File cannot be added. See ERR for details. File: {file-name}”, prints to System.err stack trace.
The default message: “File added successfully. File: {file-name}”.

detach:
void detach(String fileName, String optionalParameter)

Detaches from the last version (but does not remove from the file system) the file indicated as a parameter to the command. It has an optional parameter {“Message content in quotes}”, which can be given as the last parameter. This is the user's message. It is docked to the default message; together they will form a version message.If no file is indicated, it prints to System.out: “Please specify file to detach.”.
If a file is indicated, it:
- if successful, it prints to System.out: “File detached successfully. File: {file-name}”, and creates a new version.
- If the current directory is not initialized, it prints to System.out the message: “Current directory is not initialized. Please use “init” command to initialize.”. This error takes precedence over all others.
-	
- if the file is not added to gvt, it prints to System.out: “File is not added to gvt. File: {file-name}”
- if any other error occurred, it prints System.out: “File cannot be detached, see ERR for details. File: {file-name}”, writes to System.err stack trace.
The default message: “File detached successfully. File: {file-name}”.

checkout:
void checkout(int versionNumber)

Returns files to the state of the specific version indicated in the parameter.
The command does not change the state in which GVT controls the files. E.g.: if a file was controlled in the version being restored, and is not controlled in the last version, it will NOT add to GVT, only restore its contents (or restore if it was deleted in the meantime). Files that are not controlled in both versions remain unchanged.
It takes 1 parameter: the version number to restore.
- if the specified version is correct, restores the state of all files to the state of the specified version, and prints to System.out: “Checkout successful for version: {specified-version}”
- if the specified version is invalid (it does not exist, or it is not a number) prints to System.out: “Invalid version number: {specified-version}”.
- If the current directory is not initialized, it prints to System.out the message: “Current directory is not initialized. Please use “init” command to initialize.”. This error takes precedence over all others.
- If any other error occurred, it prints System.out: “Underlying system problem. See ERR for details.” And it prints on System.err stack trace.

  commit:
  void commit(String filename, String optionalParameter)

Creates a new version in GVT with the file specified as a parameter. Has an optional parameter {“Message content in quotes”}, which can be given as the last parameter. This is the user's message. It is docked to the default message; together they will form a version message.If no file is indicated, it prints to System.out: “Please specify file to commit.”.
If a file is indicated, then:
- if the file was added and still exists, it creates a new version, and prints to System.out: “File committed successfully. File: {file-name}”.
- if the specified version is valid, restores the state of all files to that of the specified version, and prints to System.out: “Checkout successful for version: {specified-version}”
- if the file was not added, prints to System.out: “File is not added to gvt. File: {file-name}”.
- if the file does not exist, prints to System.out: “File not found. File: {file-name}”.
- if any other error occurred, prints System.out: “File cannot be committed, see ERR for details. File: {file-name}”, writes to System.err stack trace.
Default message: “File committed successfully. File: {file-name}.”


  history:
  void history(int numberOfVersions)

Displays the version history.
Format:   
{version-number}: {commit message}.

 Each version is displayed on a new line. If the message (commit message) is multi-line, display only the first line.
- displays the last numberOfVersions of the version.
- if any other error occurred, prints System.out: “Underlying system problem. See ERR for details.” And prints to System.err stack trace.
- if no parameters are specified, displays all versions.

  version:
  void version(int versionNumber)

Displays the details of the version, with the number given as paramter.
Format: 	
Version: {version-number}
{commit message}

- if the specified version is valid, restores the state of all files to that of the specified version, and prints to System.out: “Checkout successful for version: {specified-version}”.
- if any other error occurred, prints System.out: “Underlying system problem. See ERR for details.” and prints a stack trace on System.err.
- if no parameter is specified, displays the currently active version.
- if the specified version is invalid (does not exist,) prints to System.out: “Invalid version number: {specified-number}.”.


