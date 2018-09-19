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
	
    - Authenticator Activity
        - This class handles the login/sign up flows and incorporates the basic UI provided by AWS
	- Also modify the AuthUIConfiguration to customize the login UI
        
    - List Adapters and models
        - We need to build models of the posts we create (could all be the same or different for different content formats)
        - Need to build custom list adapters that will know how to display our custom post models
	
	

Extending AWS Project - Content Streamer
    
    - Right now we have a sign in/sign up flow established
        - Add more sign up options (facebook, google, etc.)

    - Add a search bar to the top of the home feed

    - Current call structure is to connect to DynamoDB, which stores the file path for S3
        - Instead we want to use Retrofit to make API calls to AWS API services 
