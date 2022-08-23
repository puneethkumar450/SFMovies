# SFMovies
Movies Search...
**Movie Finder** is a sample android application to search movies using OMDb API which is built to demonstrate use of *Modern Android development* tools.
It has been built using kotlin with clean architecture principles and MVVM pattern as well as Architecture Components.

## About
The app in itself is a simple movie searching app. Clicking the movie list item it shows the detail of the movie. While this is not an extremely complex app, it isn't a silly Hello World one either, so the hope is that it'll cover regular use cases for a basic application.

## Sailent Features:
- Search the movies.
- View the list of movies based on the search result.
- View the released date, genre, IMDB rating and short plot.


## Architecture
This app uses [***MVVM (Model View View-Model)***](https://developer.android.com/jetpack/docs/guide#recommended-app-arch) architecture.

## Built With 🛠
- [Kotlin](https://kotlinlang.org/) - First class and official programming language for Android development.
- [Coroutines](https://kotlinlang.org/docs/reference/coroutines-overview.html) - For asynchronous and more..
- [Android Architecture Components](https://developer.android.com/topic/libraries/architecture) - Collection of libraries that help you design robust, testable, and maintainable apps.
  - [LiveData](https://developer.android.com/topic/libraries/architecture/livedata) - Data objects that notify views when the underlying database changes.
  - [ViewModel](https://developer.android.com/topic/libraries/architecture/viewmodel) - Stores UI-related data that isn't destroyed on UI changes. 
  - [ViewBinding](https://developer.android.com/topic/libraries/view-binding) - Generates a binding class for each XML layout file present in that module and allows you to more easily write code that interacts with views.
- [Koin](https://insert-koin.io) - Dependency Injection Framework
- [Retrofit](https://square.github.io/retrofit/) - A type-safe HTTP client for Android and Java.
- [GSON](https://github.com/google/gson) - A Java serialization/deserialization library to convert Java Objects into JSON and back.
- [GSON Converter](https://github.com/square/retrofit/tree/master/retrofit-converters/gson) - A Converter which uses Gson for serialization to and from JSON.
- [OkHttp3](https://github.com/square/okhttp) -  For implementing interceptor, logging and mocking web server.
- [Glide](https://github.com/bumptech/glide) - An image loading and caching library for Android focused on smooth scrolling.
- [ROOMDB](https://developer.android.com/training/data-storage/room) - The Room persistence library provides an abstraction layer over SQLite to allow fluent database access while harnessing the full power of SQLite.
- [Material Components for Android](https://github.com/material-components/material-components-android) - Modular and customizable Material Design UI components for Android.


# Package Structure
    
    com.example.sfmovies    # Root Package
    .
    ├── data                # For data handling.
    │   ├── model           # Model classes
    │   ├── local           # Local ROOM DB classes
    │   ├── network         # Remote Data Handlers     
    |   │   ├── api         # Retrofit API for remote end point.
    │   └── repository      # Single source of data.
    |
    |
    ├── ui                  # Activity/View layer
    │   ├── main            # Main Screen Activity & ViewModel
    |   │   ├── adapter     # Adapter for RecyclerView
    |   │   ├── viewmodel   # ViewHolder for RecyclerView   
    │   └── details         # Detail Screen Activity and ViewModel
    │   └── favorites       # Favorites Screen Activity and ViewModel
    |
    └── utils               # Utility Classes / Kotlin extensions


## License

```
MIT License

Copyright (c) 2020 Puneeth kumar

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.```
