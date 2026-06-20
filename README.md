# QuickCart

A grocery ordering Android app built as a personal project to practice Android development with modern architecture and Firebase. Includes an AI-powered feature using the Claude API to auto-suggest product categories in the admin panel.

## What it does

- Users can sign up, log in, browse grocery products, add them to a cart, and place orders
- Cart works offline using Room, so items aren't lost if there's no internet
- Admins (a separate role) can add new products and update order statuses
- When adding a product, the admin can tap "AI Suggest" to auto-fill the category using the Claude API instead of typing it manually

## Tech stack

- Kotlin
- MVVM architecture (ViewModel + StateFlow + Repository pattern)
- Firebase Authentication + Firestore
- Room (for local cart storage)
- Retrofit (for the Claude API calls)
- Jetpack Navigation Component
- Coil for image loading

## Why I built it this way

I wanted to practice a proper layered architecture instead of putting everything in the Activity/Fragment. So the structure is:

```
UI (Fragment) -> ViewModel -> Use Case -> Repository -> Firebase / Room / Claude API
```

Each use case handles validation (like checking if the cart is empty before placing an order) so the ViewModel stays thin and the UI layer doesn't need to know about Firestore or Room directly.

## Folder structure

```
com.ishaan.quickcart
├── ai/              -> Claude API service, repository, request/response models
├── data/
│   ├── firebase/    -> Auth, Product, Order repositories
│   ├── local/       -> Room database (cart)
│   └── model/       -> Data classes (Product, Order, User, CartItem)
├── domain/usecase/  -> Business logic for each feature
├── ui/              -> One package per screen (auth, home, cart, orders, profile, admin)
└── utils/           -> Constants, Resource wrapper, mapper functions
```

## How the AI part works

In the admin "Add Product" screen, there's an input for name and description. Tapping "AI Suggest" sends both to Claude with a prompt asking it to pick one category from a fixed list (Fruits, Vegetables, Dairy, Bakery, Beverages, Snacks, Household). The app checks that the response actually matches one of those categories before filling it in, and the admin can still change it manually if needed.

## Setup (if you want to run it)

1. Clone the repo
2. Add your own `google-services.json` from Firebase Console into the `app/` folder
3. Create a `gradle.properties` file and add:
   ```
   CLAUDE_API_KEY=your_key_here
   ```
4. In Firestore, create a `products` collection and add some products manually, and create a `users` collection where you can mark a user as admin by setting `isAdmin: true` on their document

## Things I'd improve if I had more time

- Real-time order updates instead of manual refresh
- Push notifications when order status changes
- Actual unit tests for the ViewModels
- Using Hilt instead of the manual AppModule singleton I wrote

## Note

This was built as a learning project to practice MVVM, Firebase, and integrating a third-party AI API into an Android app. A few features (push notifications, real-time listeners) were intentionally left out to keep the scope manageable.
