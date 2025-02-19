# **My Box - Project Documentation**

## **Overview**

**My Box** is an Android application that allows users to create, manage, and organize categories (called Boxes) and items within those categories. The app is designed using the **MVVM** architecture to help maintain a clean separation between UI and business logic, making the app more maintainable and scalable.

This project uses several Firebase services:
- **Firebase Storage** to store user-uploaded images.
- **Firebase Realtime Database** to store category and item data.
- **Firebase Authentication** to securely manage user sign-ins and ensure data privacy.

### **Key Features:**
- **Category Creation**: Users can create custom categories (Boxes) displayed on the main screen.
- **Item Management**: Users can add, update, and delete items inside categories.
- **User Authentication**: Firebase Auth is used for secure user login, allowing personalized data and greetings.
- **Data Storage**: Firebase Realtime Database stores user data, ensuring each user's data is isolated and secure.
  

## **Architecture**
The app follows the **MVVM (Model-View-ViewModel)** architecture, which separates the user interface (View) from the underlying logic (Model) through the ViewModel. This approach makes it easier to manage state, handle complex logic, and test different components of the app.

## **Technologies Used**
- **Kotlin**: Programming language used to build the app.
- **Firebase Authentication**: Manages user authentication and security.
- **Firebase Realtime Database**: Stores user data, categories, and items in real time.
- **Firebase Storage**: Stores images uploaded by users.

## **Firebase Database Structure**
The Realtime Database is structured as follows: 

users -> {UID} -> categories -> {categoryId} -> items -> {itemId}

![image](https://github.com/Zidantfnno21/MyBox/assets/98997038/bf4edf5c-a8d7-4b36-9032-1d3b5d2f4f0f)

### **Explanation**:
- **users**: The top-level node where all user data is stored. Each user is identified by their **UID** (unique user ID) provided by Firebase Authentication.
- **{UID}**: Each user’s data is stored under their unique UID, ensuring that data is isolated and private for each user.
- **categories**: Under each user’s UID, there is a **categories** node, which contains all the categories (or "Boxes") that the user has created.
- **{categoryId}**: Each category is identified by a unique **categoryId**.
- **items**: Inside each category, there is an **items** node, where the individual items within that category are stored.
- **{itemId}**: Each item is identified by a unique **itemId** within the category.

## **How to Run the App Locally**

To run this app on your local machine, follow these steps:

1. Clone the repository:
   ```bash
   git clone https://github.com/Zidantfnno21/MyBox.git
2. Open the project in Android Studio.
3. Make sure you have the required Firebase services set up:
   * Create a project in the Firebase Console.
   * Set up Firebase Authentication, Firebase Realtime Database, and Firebase Storage in the console.
   * Download the **google-services.json** file and add it to the `app` folder of your project.
4. Build and run the app on your Android device or emulator.

## **App Features & Demos**

### **1. Create Category (Box)**
Users can create their own categories, which are displayed on the main screen.
- **Demo**: [Create Category](https://github.com/Zidantfnno21/MyBox/assets/98997038/a34a8640-996b-4f0b-8e8b-93b231495850)

### **2. Create Item**
Once a category is created, users can add items to that category.
- **Demo**: [Create Item](https://github.com/Zidantfnno21/MyBox/assets/98997038/aaa95789-3d17-425d-813f-d0db4dce651e)

### **3. Update Items/Category**
Users can update their items or categories by pressing the "update" button.
- **Demo**: [Update Item/Category](https://github.com/Zidantfnno21/MyBox/assets/98997038/ce00d6cb-98cd-4810-839d-7180976026be)

### **4. Delete Items/Category**
Users can delete items or categories by pressing the "delete" button.
- **Demo**: [Delete Item/Category](https://github.com/Zidantfnno21/MyBox/assets/98997038/38137768-efda-4b7f-b4df-a5888bd17e7a)
