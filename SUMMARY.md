# Summary

The application is used for managing files. It is possible to upload files, request their metadata and delete them. Metadata receiving and deleting works with file token which is received after upload.

Files are saved on disk, token with filename and metadata is saved in database.

API documentation: http://localhost:6011/docs

For starting:
- Download zip and unpack it.
- In file `src\main\kotlin\com\hrblizz\fileapi\controller\filecontroller\FileControllerConstants.kt` change the directory path, where the files are uploaded
- In Command Line: `docker-compose up -d`
- In Git Bash to start application: `./do.sh start`

## Comments

I liked the assignment, since I learned a lot that I would not learn by doing my hobby projects. I understood generally, what I should do, and I knew what is needed for that, but had to research, how I should do it in Kotlin. Some questions appeared during the assignment: should I do something in x or y way? Added some questions below.

## Which part of the assignment took the most time and why?
Project setup. Did it twice: before coding and before writing in `SUMMARY.md` how to start it.

I do not have experience with Docker so everything that was related to it, took time to understand, what should I do. I just googled/asked ChatGPT, because I did not have a lot of time to learn it. Definitely I have to learn Docker basics to understand it better.

## What You learned

- A bit of Spring Framework: handling different requests.
- Reading foreign Kotlin projects: using and adding new features to existing files.
- Using Docker and MongoDB Compass.
- Refreshing my knowledge of Kotlin.
- In Intellij, when hover a function, it shows what exceptions may be thrown in it. 
- Using tool for creating API documentation.

## TODOs
- Learn how Docker works
- Some additional questions for the "customer"
    - What should happen when a file with the same name as the uploaded file already exists?
    - What does `expireTime` mean? Does it mean when the `expireTime` has passed, then the file and/or metadata are not available?
    - Are files editable? Should the `size` value come from database, or should it always be obtained from the file itself?
- Some additional questions for the other developer or finding in Internet
  - If someone is asking metadata about tokens and given token does not exist in database, how to give information about it? Or just skip them?
  - Why instead of for example `NOT_FOUND` error the assignment is requiring only to return `400` or `503`?
  - ErrorMessages have also `code` parameter. This is place to come up with my custom codes? For example if token is not found, then the code can be `TOKEN_NOT_FOUND`?
  - Handling constants like the class FileControllerConstants. How they should be stored?
  - What should be returned after `DELETE` request?
- The API documentation is not consistent and the error codes probably could described better.
- Code can be refactored. Some repeating code can be moved to one function.
- Meta field in `files/metas` request has to be returned in JSON object, not JSON object string.
  - First thought
    - Make a new object, that takes next fields: file token, json field name, value. It is a good way to store information, when it is not known how many fields and what fields can be.
    - Connect somehow the new object with Entity one, if it is possible. Then the class FileMetadataController probably does not need changes. 
- Add authentication to API
