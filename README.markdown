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
Full instructions for using this module are forthcoming. Stay
tuned!

Dependencies
------------
None at present.

Version
-------
This is version 0.1 of this module, meaning it is pretty darn
new.

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
