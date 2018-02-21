/*

The MIT License (MIT)

Copyright (c) 2015 Marius Thiemann <marius dot thiemann at ploin dot de>

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
SOFTWARE.

 */

var SlidablePanel = (function() {

	// based on bootstrap
	var makeSlidable = function(panel) {
		var head = $(panel).children(".panel-heading");
		var body = $(panel).children(".panel-body");

		if (!head)
			return;

		if (!body)
			return;

		head.append("<span class=\"pull-right clickable\"></span>");

		var clickable = head.children(".clickable");

		if (!clickable)
			return;

		clickable.append("<i class=\"glyphicon glyphicon-chevron-down\"></i>");
		clickable.css("margin-top", "-15px");

		clickable.on("click", function() {
			_toogleSlide(clickable, body);
		});

		body.slideUp();
	};

	var makeOpenSlidable = function(panel) {
		var head = $(panel).children(".panel-heading");
		var body = $(panel).children(".panel-body");

		if (!head)
			return;

		if (!body)
			return;

		head.append("<span class=\"pull-right clickable\"></span>");

		var clickable = head.children(".clickable");

		if (!clickable)
			return;

		clickable.append("<i class=\"glyphicon glyphicon-chevron-up\"></i>");
		clickable.css("margin-top", "-15px");

		clickable.on("click", function() {
			_toogleSlide(clickable, body);
		});

		body.slideDown();
	};

	var _toogleSlide = function(clickable, body) {
		var glyphicon = clickable.children(".glyphicon");

		if (glyphicon.hasClass("glyphicon-chevron-down")) {
			glyphicon.removeClass("glyphicon-chevron-down");
			glyphicon.addClass("glyphicon-chevron-up");
			body.slideDown();
		} else if (glyphicon.hasClass("glyphicon-chevron-up")) {
			glyphicon.removeClass("glyphicon-chevron-up");
			glyphicon.addClass("glyphicon-chevron-down");
			body.slideUp();
		}
	};

	return {
		makeSlidable : makeSlidable,
		makeOpenSlidable : makeOpenSlidable
	};
})();