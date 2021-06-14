
A database file "Tshiorny_Romi_backend.sql" is provided to create the database on your local machine. (Done in MicrosoftSQL). File wont run unless this database exists locally.
Once you create the database, download the Astrobase.jar and open it with an archive navigator (7-zip, Winzip, etc.). 

Navigate to 'model >connect.txt' and open the file.
Replace the given credentials with the ones you must use to access your data
base. The format of the file is as follows:

	1. Database path
	2. Database username
	3. Database password
	4. Database name

The UI is able to do SELECT, INSERT, UPDATE, and DELETE calls on the database.

	The SELECT utility can be seen whenever a user clicks any value in a
	non-left column. All the properties of the object are displayed
	below. The user can navigate away from and into objects to see what children
	they contain. (I.E a SolarSystem has Planets, Stars, etc..).

	The INSERT utilty can be seen whenever the user clicks the '+' button to insert
	a value into any selected non-left column. The user is prompted for the name and
	then it is inserted into the table and the database.

	The UPDATE utility can be seen when the user clicks the edit button. To edit the
	attributes simply select an object, press edit, and then type into the categories.
	Once update is hit those changes are saved to the data base. To cancel an edit
	simply navigate away to another object. *NOTICE* There is no advanced type checking
	so some incorrect inputs might result in an error in the console with no changes made.

	The DELETE utility can be seen whenever the user selects an object, and pressed delete.
	The object is promptly removed from the table and the database.
	
NOTE: Note every table gets its fully utility used in this program due to time constraints, however a good amount of the utility is implemented.
