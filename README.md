# android-dev-ynov

Git repository about my work for mobile development during Ynov classes.
The end of study project is contained into `scheduleapp/`

This following readme will describe the system and app's architecture.

## App access : <last name> <password>

- As Carl (child): redwall - prince
- As Davy (child): redwall - little
- As Roger (parent): redwall - king
Feel free to send me a message if you want to defined phone number for Roger 

## Available functionnalities

- Connection to the app with a last name and password couple
- Last name is shared between a whole family and passwords are unique to each user (child or parent)
- If required permissions aren't allowed, the main activity lock login and purposes an access to the app permission management screen.

### Children

- Have a view on scheduled tasks he has to complete
- Can see if a scheduled task is late
- Can have some details on a specific task like recurrence or scheduled reminders
- Can set a task as done or manually send a generated SMS to their parents and prevent late end
- On app close or activity close, all reminders and notification are programmed to be triggered at a specific time (task's end, task reminder scheduled before or after task's end)
- If a reminder is planned and the next task begins in less 5 minutes, parents are automatically warned about the task's late

### Parents

- Are warned only one time via SMS for late, for one task
- Can have a view for scheduled tasks for this children
- For each of this children, one parent can add a task for a specific child
- He can define for the task:  name, type, date and time begin, duration (optionally recurrence or reminders)
- For a specific scheduled task, he can delete the task, it recurrence or it reminder(s)

## Used paradigm and functional structure

The main paradigm used in this project is reactive programming through `rxjava` and `rxandroid` libraries.

I've the choice to develop with these following libraries because I was interested in discovering reactive programming inside an Android app. I have make the choice to correspond too with Firebase listening data changes mechanism. 

These listeners are stored into unique `actors/` (singleton). In the system, actors will contains the functional and business logic to complete the multiples functionalities. To populate the view on the reactive way, actors will expose data through behavior subject. They correspond to a stream with a default value, disallowing null value inside observers during the subscription.

## Possible roadmap

- Cleaning and refactoring
- Implement all CRUD operations for tasks, family ... 
- Responsive view follow device resolution
- Add some messages or possible interactions with the user (when a child complete a task for example)
- Allow family management
- Add a dashboard about a count of late tasks for example