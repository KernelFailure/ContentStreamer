# ContentStreamer
Original Commit - early testing

AWS Backend Project

- create proof of concept app that encompasses/shows off some of the sweet new AWS skills we've acquired over the last couple days 

- specifically, we want to include the following AWS features:
    - Mobile Hub
    - Cognito to sign in/out and authenticate users
	- I want to enable the full palette of sign in options/OAuth (Gmail, Facebook, AWS, others they might offer)
    - DynamoDB to make CRUD operations
    - Pinpoint for analytics
    - S3 for storing images, videos, soundfiles

- Implementation
    - This simple, proof of concept app will allow users to sign in, create a profile and then upload content to the servers as 
        well as view content that others have posted

    - Cognito will be used to for sign in/out, identity management, etc.
    - DynamoDB will be used to store user preferences, user info, content info
    - S3 will be used to store and deliver the actual content that users upload/stream
    - Pinpoint will be incorporated to track popular content, user queries

Class Structure 

    - AWSProvider
        - This class will provide AWS credentials/functions and most of the AWS implementations should originate in this class
        
    - Home Feed
        - Users can scroll through content or search for specific content
        
    - Profile
        - Users can view/edit basic profile info (username, email, profile picture)
        - Users can upload content stored on their phone
        
    - List Adapters and models
        - We need to build models of the posts we create (could all be the same or different for different content formats)
        - Need to build custom list adapters that will know how to display our custom post models
	
	

Extending AWS Project - Content Streamer
    - Find a cooler icon

    - Right now, the S3 query and the file placement path is hard coded
        - Allow for dynamic queries
        - Make available files visible so users don't have to guess/know the paths they're looking for
        - For now, users should just be able to select a file that is displayed for download.  Adding search inputs will come later

    - Right now we have a list view with a custom post adapter in our home feed
        - Might have to update the adapter and model post class depending on the type of info we get from our downloads
        - We should be able to get the file name, file size from any S3 download
            - do some research on all the other info you can get from a standard S3 download
            - This might be where DynamoDB can help out, storing and serving meta data about our files (date, genre, author, likes, etc.)
                - Have to figure out how to work the Lambda/AppSync functionalities in order to simplify our read/write methods


How To:

- set up the listview with the available content on S3
    - We should still have the List<ContentPost> variable.  After updating the ContentPost model class that list should still be good to go
    - We'd have to check the sample app on AWS and the way that they looped through the available files
        - Looks like they set up a list of TransferObservers and utilized hash maps 
