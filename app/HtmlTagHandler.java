
/*
* Copyright (C) 2013-2014 Dominik Schürmann <dominik@dominikschuermann.de>
* Copyright (C) 2013 Mohammed Lakkadshaw
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package ca.nicholaspaun.chemlogic.app1;
import java.util.Vector;

import org.xml.sax.XMLReader;

import android.graphics.Color;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.style.AlignmentSpan;
import android.text.style.BackgroundColorSpan;
import android.text.style.BulletSpan;
import android.text.style.LeadingMarginSpan;
import android.text.style.TypefaceSpan;
import android.util.Log;
/**
* Some parts of this code are based on android.text.Html
*/
public class HtmlTagHandler implements Html.TagHandler {
private int mListItemCount = 0;
private Vector<String> mListParents = new Vector<String>();
private boolean DEBUG = true;

private static class Code {
}
private static class Center {
}

private static class BgColor {
	private String color;
	
	public BgColor(String i_color)
	{
		color = i_color;
	}
}

@Override
public void handleTag(final boolean opening, final String tag, Editable output, final XMLReader xmlReader) {
if (opening) {
// opening tag
if (DEBUG) {
Log.d("chemlogic", "opening, output: " + output.toString());
}
if (tag.equalsIgnoreCase("ul") || tag.equalsIgnoreCase("ol") || tag.equalsIgnoreCase("dd")) {
mListParents.add(tag);
mListItemCount = 0;
} else if (tag.equalsIgnoreCase("code")) {
start(output, new Code());
} else if (tag.equalsIgnoreCase("center")) {
start(output, new Center());
} else if (tag.startsWith("bgcolor-"))
{
start(output, new BgColor(tag.substring(8)));	
}
} else {
// closing tag
if (DEBUG) {
Log.d("chemlogic", "closing, output: " + output.toString());
}
if (tag.equalsIgnoreCase("ul") || tag.equalsIgnoreCase("ol") || tag.equalsIgnoreCase("dd")) {
mListParents.remove(tag);
mListItemCount = 0;
} else if (tag.equalsIgnoreCase("li")) {
handleListTag(output);
} else if (tag.equalsIgnoreCase("code")) {
end(output, Code.class, new TypefaceSpan("monospace"), false);
} else if (tag.equalsIgnoreCase("center")) {
end(output, Center.class, new AlignmentSpan.Standard(Layout.Alignment.ALIGN_CENTER), true);
} else if (tag.startsWith("bgcolor-"))
{
end(output, BgColor.class, new BackgroundColorSpan(Color.parseColor("#" + tag.substring(8))),false);
}
}
}
/**
* Mark the opening tag by using private classes
*
* @param output
* @param mark
*/
private void start(Editable output, Object mark) {
int len = output.length();
output.setSpan(mark, len, len, Spannable.SPAN_MARK_MARK);
if (DEBUG) {
Log.d("chemlogic", "len: " + len);
}
}
private void end(Editable output, Class kind, Object repl, boolean paragraphStyle) {
Object obj = getLast(output, kind);
// start of the tag
int where = output.getSpanStart(obj);
// end of the tag
int len = output.length();
output.removeSpan(obj);
if (where != len) {
// paragraph styles like AlignmentSpan need to end with a new line!
if (paragraphStyle) {
output.append("\n");
len++;
}
output.setSpan(repl, where, len, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
}
if (DEBUG) {
Log.d("chemlogic", "where: " + where);
Log.d("chemlogic", "len: " + len);
}
}
/**
* Get last marked position of a specific tag kind (private class)
*
* @param text
* @param kind
* @return
*/
private Object getLast(Editable text, Class kind) {
Object[] objs = text.getSpans(0, text.length(), kind);
if (objs.length == 0) {
return null;
} else {
for (int i = objs.length; i > 0; i--) {
if (text.getSpanFlags(objs[i - 1]) == Spannable.SPAN_MARK_MARK) {
return objs[i - 1];
}
}
return null;
}
}
private void handleListTag(Editable output) {
if (mListParents.lastElement().equals("ul")) {
output.append("\n");
String[] split = output.toString().split("\n");
int lastIndex = split.length - 1;
int start = output.length() - split[lastIndex].length() - 1;
output.setSpan(new BulletSpan(15 * mListParents.size()), start, output.length(), 0);
} else if (mListParents.lastElement().equals("ol")) {
mListItemCount++;
output.append("\n");
String[] split = output.toString().split("\n");
int lastIndex = split.length - 1;
int start = output.length() - split[lastIndex].length() - 1;
output.insert(start, mListItemCount + ". ");
output.setSpan(new LeadingMarginSpan.Standard(15 * mListParents.size()), start, output.length(), 0);
}
}
} 
