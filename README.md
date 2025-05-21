# Donœur - Blood Donation App

Donœur is a modern Android application that connects blood donors with those in need, providing a seamless experience for posting requests, chatting, and tracking donation history.

## Features

- **Home Feed**: View real-time posts from people seeking blood donations, fetched directly from Firestore.
- **Add Post**: Create and publish new posts requesting or offering blood donations. Successful publication is confirmed with a notification.
- **Chats**: Professional chat interface allowing users to start new conversations, view chat history, and exchange messages in real time.
- **Profile**: View and edit your profile, see donation eligibility, and access your donation history in a popup dialog.
- **Blood Type Compatibility Checker**: Easily check blood type compatibility for donations.
- **Authentication**: Secure Sign In/Sign Up using Firebase Authentication.
- **Firestore Integration**: All posts and donation history are stored and fetched from Firestore.
- **Responsive UI**: Built with Jetpack Compose for a modern, scrollable, and responsive user interface.
- **Fixed Bottom Navigation**: Persistent navigation bar that does not overlay content.

## Tech Stack

- **Frontend**: Kotlin, Jetpack Compose
- **Backend**: Firebase Authentication, Firestore, Realtime Database (for chats)
- **Architecture**: MVVM-inspired, Compose-first

## How It Works

1. **Sign Up / Sign In**: Users authenticate securely with Firebase.
2. **Home Feed**: Users see all posts, including urgent blood requests, updated in real time.
3. **Add Post**: Users can write and submit new posts, which appear instantly in the home feed.
4. **Chats**: Users can start new chats by searching for other users, and chat histories are preserved.
5. **Profile**: Users can view their stats, update their info, and see their donation history in a popup.
6. **Donation Eligibility**: The app tracks donation cooldowns and eligibility based on user data.

## Screenshots

> _Add screenshots here to showcase the UI and features._

## Getting Started

1. Clone the repository.
2. Open in Android Studio.
3. Set up Firebase for your project and add the `google-services.json` file.
4. Build and run on an Android device or emulator.

## Contribution

Pull requests are welcome! For major changes, please open an issue first to discuss what you would like to change.

## License

[MIT](LICENSE)

```