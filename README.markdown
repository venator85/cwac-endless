CWAC EndlessAdapter: It Just Keeps Going and Going And...
=========================================================

AJAX Web sites have sometimes taken up the "endless page"
model, where scrolling automatically loads in new content,
so you never have to click a "Next" link or anything like that.

Wouldn't it be cool to have that in an Android application?
Kinda like how the Android Market does it?

`EndlessAdapter` is one approach to solving this problem.

It is designed to wrap around another adapter, where you have
your "real" data. Hence, it follows the Decorator pattern,
augmenting your current adapter with new Endless Technology(TM).

To use it, you extend `EndlessAdapter` to provide details about
how to handle the endlessness. Specifically, you need to be
able to provide a row `View`, independent from any of the rows
in your actual adapter, that will serve as a placeholder
while you, in another method, load in the actual data to
your main adapter. Then, with a little help from you, it
seamlessly transitions in the new data.

So, this is not truly "endless" insofar as the user does see
when we load in new data. However, it should work well for
Android applications backed by Web services or the like
that work on "page-at-a-time" metaphors -- users get the
additional data quickly and do not incur the bandwidth to
download that data until and unless they scroll all the
way to the bottom.

Usage
-----
To use `EndlessAdapter`, you need to create a subclass that
will control the endlessness, specifying what `View` to use
for the "loading" placeholder, and then updating that placeholder
with an actual row once data has been loaded.

`EndlessAdapter` assumes there is at least one more "batch" of
data to be fetched. If everything was retrieved for your
`ListAdapter` the first time out (e.g., the Web search returned
only one "page" of results), do not wrap it in `EndlessAdapter`,
and your users will not perceive a difference.

### Constructors

`EndlessAdapter` has one constructor, taking a `ListAdapter` as
a parameter, representing the existing adapter to be made
endless. Your `EndlessAdapter` subclass will need to override
this constructor and chain upwards. For example, the DemoAdapter
inside the demo project takes an `ArrayList<String>` as a
constructor parameter and wraps it in a `ListAdapter` to supply
to `EndlessAdapter`.

### The Placeholder

Your `EndlessAdapter` subclass needs to implement `getPendingView()`.
This method works a bit like the traditional `getView()`, in that
it receives a `ViewGroup` parameter and is supposed to return a
row `View`. The major difference is that this method needs to
return a row `View` that can serve as a placeholder, indicating
to the user that you are fetching more data in the background
(see below). However, this same row `View` must also be able to
convert in-place to a regular row in your list.

What sort of `View` you return, of course, is up to you. The
demo application uses a row that, via a `FrameLayout`, has both
a `TextView` (the normal row content) and an `ImageView` (placeholder)
overlaying each other. In placeholder mode, only the `ImageView`
is visible, and it is set to rotate via a `RotateAnimation`. In
normal mode, only the `TextView` is visible.

### The Loading

Your `EndlessAdapter` subclass also needs to implement `cacheInBackground()`.
This method will be called from a background thread, and it needs
to download more data that will eventually be added to the `ListAdapter`
you used in the constructor.
While the demo application simply sleeps for two seconds, a real
application might make a Web service call or otherwise load in
more data.

This method returns a `boolean`, which needs to be `true` if there
is more data yet to be fetched, `false` otherwise. Hence, you need
to make sure that, by the time you return, you know whether or
not there is more data available.

Since this method is called on a background thread, you do not
need to fork your own thread. However, at the same time, do not
try to update the UI directly.

### The Attaching

Your `EndlessAdapter` subclass also needs to implement `appendCachedData()`,
which should take the data cached by `cacheInBackground()` and append
it to the `ListAdapter` you used in the constructor. While
`cacheInBackground()` is called on a background thread,
`appendCachedData()` is called on the main application thread.

### The Rebinding

Your `EndlessAdapter` also needs to implement `rebindPendingView()`.
This method will be called, on the UI thread, after `appendInBackground()`
completes its work. You will be passed the position in the
`ListAdapter` that needs to go in this row, plus the original row
`View` itself. Your mission is to make the row `View` look like
any other row (e.g., replace the "loading" graphic with
the actual row content).

Dependencies
------------
This project relies upon the [CWAC AdapterWrapper][adapter] project.
A copy of compatible JARs can be found in the `libs/` directory
of the project, though you are welcome to try newer ones, or
ones that you have patched yourself.

Version
-------
This is version 0.2 of this module, meaning it is pretty darn
new, but is getting more exercise.

Demo
----
In the `com.commonsware.cwac.endless.demo` package you will find
a sample activity that demonstrates the use of `EndlessAdapter`.

Note that when you build the JAR via `ant jar`, the sample
activity is not included, nor any resources -- only the
compiled classes for the actual library are put into the JAR.

License
-------
The code in this project is licensed under the Apache
Software License 2.0, per the terms of the included LICENSE
file.

Questions
---------
If you have questions regarding the use of this code, please
join and ask them on the [cw-android Google Group][gg]. Be sure to
indicate which CWAC module you have questions about.

[gg]: http://groups.google.com/group/cw-android
[adapter]: http://github.com/commonsguy/cwac-adapter/tree/master