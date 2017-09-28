/*
 * Apache License
 * Version 2.0, January 2004
 * http://www.apache.org/licenses/
 *
 *    Copyright 2013 Aurelian Tutuianu
 *    Copyright 2014 Aurelian Tutuianu
 *    Copyright 2015 Aurelian Tutuianu
 *    Copyright 2016 Aurelian Tutuianu
 *    Copyright 2017 Aurelian Tutuianu
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package rapaio.experiment.printer;

import rapaio.graphics.base.Figure;
import rapaio.printer.AbstractPrinter;
import rapaio.printer.Printer;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Base64;

/**
 * @author <a href="mailto:padreati@yahoo.com">Aurelian Tutuianu</a>
 */
@Deprecated
public class HTMLPrinter extends AbstractPrinter {

    private final String title;
    private final String backLink;
    private final PrintWriter writer;
    private int textWidth = 80;
    private int graphicWidth = 500;
    private int graphicHeight = 250;

    public HTMLPrinter(String fileName, String title) {
        this(fileName, title, "");
    }

    public HTMLPrinter(String fileName, String title, String backLink) {
        this.title = title;
        this.backLink = backLink;
        try {
            this.writer = new PrintWriter(new BufferedWriter(new FileWriter(new File(fileName))));
        } catch (IOException ex) {
            throw new RuntimeException("Could not initialize HTML document", ex);
        }
    }

    @Override
    public int textWidth() {
        return textWidth;
    }

    @Override
    public Printer withTextWidth(int textWidth) {
        this.textWidth = textWidth;
        return this;
    }

    @Override
    public int graphicWidth() {
        return graphicWidth;
    }

    @Override
    public void withGraphicWidth(int graphicWidth) {
        this.graphicWidth = graphicWidth;
    }

    @Override
    public int graphicHeight() {
        return graphicHeight;
    }

    @Override
    public void withGraphicHeight(int graphicHeight) {
        this.graphicHeight = graphicHeight;
    }

    @Override
    public void openPrinter() {
        String header = Template.header;
        header = header.replace(Template.KEY_TITLE, title);
        header = header.replace(Template.BACKLINK, backLink);
        writer.append(header);
    }

    @Override
    public void closePrinter() {
        String footer = Template.footer;
        footer = footer.replaceAll(Template.BACKLINK, backLink);
        writer.append(footer);
        writer.flush();
        writer.close();
    }

    public void flush() {
        writer.flush();
    }

    @Override
    public void print(String message) {
        writer.append(message);
    }

    @Override
    public void println() {
        writer.append("</br>\n");
    }

    @SuppressWarnings("deprecation")
    @Override
    public void draw(Figure figure, int width, int height) {
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics g = newImage.getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Rectangle rect = new Rectangle(newImage.getWidth(), newImage.getHeight());
        figure.paint(g2d, rect);
        byte[] imageString;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try {
            ImageIO.write(newImage, "png", bos);
            byte[] imageBytes = bos.toByteArray();
            Base64.Encoder encoder = Base64.getEncoder();
            imageString = encoder.encode(imageBytes);
            bos.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not produce image", e);
        }
        writer.append("<p><center><img src=\"data:image/png;base64,").append(String.valueOf(imageString)).append("\" alt=\"graphics\"/></center></p>\n");
    }

    @Override
    public void error(String message, Throwable throwable) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void head(int h, String lines) {
        writer.append("<h").append(String.valueOf(h)).append(">").append(lines).append("</h").append(String.valueOf(h)).append(">\n");
    }

    @Override
    public void code(String lines) {
        writer.append("<pre><code class=\"Java\">").append(lines).append("</code></pre>\n");
    }

    @Override
    public void p(String lines) {
        writer.append("<p>").append(lines).append("</p>");
    }
}

@Deprecated
class Template {

    static final String KEY_TITLE = "#TITLE#";
    static final String BACKLINK = "#BACKLINK#";
    static final String header = "<!DOCTYPE html>\n"
            + "<html>\n"
            + "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\"/>\n"
            + "<title>#TITLE#</title>\n"
            + "<style type=\"text/css\">\n"
            + "body, td {\n"
            + "   font-family: sans-serif;\n"
            + "   background-color: white;\n"
            + "   font-size: 14px;\n"
            + "   margin: 8px;\n"
            + "   width: 800px;\n"
            + "}\n"
            + "tt, code, pre {\n"
            + "   font-family: 'DejaVu Sans Mono', 'Droid Sans Mono', 'Lucida Console', Consolas, Monaco, monospace;\n"
            + "}\n"
            + "h1 { font-size:2.2em; }\n"
            + "h2 { font-size:1.8em; }\n"
            + "h3 { font-size:1.4em; }\n"
            + "h4 { font-size:1.0em; }\n"
            + "h5 { font-size:0.9em; }\n"
            + "h6 { \n"
            + "   font-size:0.8em; \n"
            + "}\n"
            + "a:visited {\n"
            + "   color: rgb(50%, 0%, 50%);\n"
            + "}\n"
            + "pre {	\n"
            + "   font-size: 0.9em;\n"
            + "   margin-top: 0;\n"
            + "   max-width: 95%;\n"
            + "   border: 1px solid #ccc;\n"
            + "   white-space: pre-wrap;\n"
            + "}\n"
            + "pre code {\n"
            + "   display: block; padding: 0.5em;\n"
            + "}\n"
            + "code.r, code.java {\n"
            + "   background-color: #F8F8F8;\n"
            + "}\n"
            + "table, td, th {\n"
            + "  border: none;\n"
            + "}\n"
            + "blockquote {\n"
            + "   color:#666666;\n"
            + "   margin:0;\n"
            + "   padding-left: 1em;\n"
            + "   border-left: 0.5em #EEE solid;\n"
            + "}\n"
            + "hr {\n"
            + "   height: 0px;\n"
            + "   border-bottom: none;\n"
            + "   border-top-width: thin;\n"
            + "   border-top-style: dotted;\n"
            + "   border-top-color: #999999;\n"
            + "}\n"
            + "@media print {\n"
            + "   * { \n"
            + "      background: transparent !important; \n"
            + "      color: black !important; \n"
            + "      filter:none !important; \n"
            + "      -ms-filter: none !important; \n"
            + "   }\n"
            + "   body { \n"
            + "      font-size:12pt; \n"
            + "      max-width:100%; \n"
            + "   }\n"
            + "       \n"
            + "   a, a:visited { \n"
            + "      text-decoration: underline; \n"
            + "   }\n"
            + "   hr { \n"
            + "      visibility: hidden;\n"
            + "      page-break-before: always;\n"
            + "   }\n"
            + "   pre, blockquote { \n"
            + "      padding-right: 1em; \n"
            + "      page-break-inside: avoid; \n"
            + "   }\n"
            + "   tr, img { \n"
            + "      page-break-inside: avoid; \n"
            + "   }\n"
            + "   img { \n"
            + "      max-width: 100% !important; \n"
            + "   }\n"
            + "   @page :left { \n"
            + "      margin: 15mm 20mm 15mm 10mm; \n"
            + "   }\n"
            + "     \n"
            + "   @page :right { \n"
            + "      margin: 15mm 10mm 15mm 20mm; \n"
            + "   }\n"
            + "   p, h2, h3 { \n"
            + "      orphans: 3; widows: 3; \n"
            + "   }\n"
            + "   h2, h3 { \n"
            + "      page-break-after: avoid; \n"
            + "   }\n"
            + "}\n"
            + "</style>\n"
            + "<!-- Styles for R syntax highlighter -->\n"
            + "<style type=\"text/css\">\n"
            + "   pre .operator,\n"
            + "   pre .paren {\n"
            + "     color: rgb(104, 118, 135)\n"
            + "   }\n"
            + "   pre .literal {\n"
            + "     color: rgb(88, 72, 246)\n"
            + "   }\n"
            + "   pre .number {\n"
            + "     color: rgb(0, 0, 205);\n"
            + "   }\n"
            + "   pre .comment {\n"
            + "     color: rgb(76, 136, 107);\n"
            + "   }\n"
            + "   pre .keyword {\n"
            + "     color: rgb(0, 0, 255);\n"
            + "   }\n"
            + "   pre .identifier {\n"
            + "     color: rgb(0, 0, 0);\n"
            + "   }\n"
            + "   pre .string {\n"
            + "     color: rgb(3, 106, 7);\n"
            + "   }\n"
            + "</style>\n"
            + "<!-- R syntax highlighter -->\n"
            + "<script type=\"text/javascript\">\n"
            + "var hljs=new function(){function m(p){return p.replace(/&/gm,\"&amp;\").replace(/</gm,\"&lt;\")}function f(r,q,p){return RegExp(q,\"m\"+(r.cI?\"i\":\"\")+(p?\"g\":\"\"))}function b(r){for(var p=0;p<r.childNodes.length;p++){var q=r.childNodes[p];if(q.nodeName==\"CODE\"){return q}if(!(q.nodeType==3&&q.nodeValue.match(/\\s+/))){break}}}function h(t,s){var p=\"\";for(var r=0;r<t.childNodes.length;r++){if(t.childNodes[r].nodeType==3){var q=t.childNodes[r].nodeValue;if(s){q=q.replace(/\\n/g,\"\")}p+=q}else{if(t.childNodes[r].nodeName==\"BR\"){p+=\"\\n\"}else{p+=h(t.childNodes[r])}}}if(/MSIE [678]/.test(navigator.userAgent)){p=p.replace(/\\r/g,\"\\n\")}return p}function a(s){var r=s.className.split(/\\s+/);r=r.concat(s.parentNode.className.split(/\\s+/));for(var q=0;q<r.length;q++){var p=r[q].replace(/^language-/,\"\");if(e[p]){return p}}}function c(q){var p=[];(function(s,t){for(var r=0;r<s.childNodes.length;r++){if(s.childNodes[r].nodeType==3){t+=s.childNodes[r].nodeValue.length}else{if(s.childNodes[r].nodeName==\"BR\"){t+=1}else{if(s.childNodes[r].nodeType==1){p.push({event:\"start\",offset:t,node:s.childNodes[r]});t=arguments.callee(s.childNodes[r],t);p.push({event:\"stop\",offset:t,node:s.childNodes[r]})}}}}return t})(q,0);return p}function k(y,w,x){var q=0;var z=\"\";var s=[];function u(){if(y.length&&w.length){if(y[0].offset!=w[0].offset){return(y[0].offset<w[0].offset)?y:w}else{return w[0].event==\"start\"?y:w}}else{return y.length?y:w}}function t(D){var A=\"<\"+D.nodeName.toLowerCase();for(var B=0;B<D.attributes.length;B++){var C=D.attributes[B];A+=\" \"+C.nodeName.toLowerCase();if(C.value!==undefined&&C.value!==false&&C.value!==null){A+='=\"'+m(C.value)+'\"'}}return A+\">\"}while(y.length||w.length){var v=u().splice(0,1)[0];z+=m(x.substr(q,v.offset-q));q=v.offset;if(v.event==\"start\"){z+=t(v.node);s.push(v.node)}else{if(v.event==\"stop\"){var p,r=s.length;do{r--;p=s[r];z+=(\"</\"+p.nodeName.toLowerCase()+\">\")}while(p!=v.node);s.splice(r,1);while(r<s.length){z+=t(s[r]);r++}}}}return z+m(x.substr(q))}function j(){function q(x,y,v){if(x.compiled){return}var u;var s=[];if(x.k){x.lR=f(y,x.l||hljs.IR,true);for(var w in x.k){if(!x.k.hasOwnProperty(w)){continue}if(x.k[w] instanceof Object){u=x.k[w]}else{u=x.k;w=\"keyword\"}for(var r in u){if(!u.hasOwnProperty(r)){continue}x.k[r]=[w,u[r]];s.push(r)}}}if(!v){if(x.bWK){x.b=\"\\\\b(\"+s.join(\"|\")+\")\\\\s\"}x.bR=f(y,x.b?x.b:\"\\\\B|\\\\b\");if(!x.e&&!x.eW){x.e=\"\\\\B|\\\\b\"}if(x.e){x.eR=f(y,x.e)}}if(x.i){x.iR=f(y,x.i)}if(x.r===undefined){x.r=1}if(!x.c){x.c=[]}x.compiled=true;for(var t=0;t<x.c.length;t++){if(x.c[t]==\"self\"){x.c[t]=x}q(x.c[t],y,false)}if(x.starts){q(x.starts,y,false)}}for(var p in e){if(!e.hasOwnProperty(p)){continue}q(e[p].dM,e[p],true)}}function d(B,C){if(!j.called){j();j.called=true}function q(r,M){for(var L=0;L<M.c.length;L++){if((M.c[L].bR.exec(r)||[null])[0]==r){return M.c[L]}}}function v(L,r){if(D[L].e&&D[L].eR.test(r)){return 1}if(D[L].eW){var M=v(L-1,r);return M?M+1:0}return 0}function w(r,L){return L.i&&L.iR.test(r)}function K(N,O){var M=[];for(var L=0;L<N.c.length;L++){M.push(N.c[L].b)}var r=D.length-1;do{if(D[r].e){M.push(D[r].e)}r--}while(D[r+1].eW);if(N.i){M.push(N.i)}return f(O,M.join(\"|\"),true)}function p(M,L){var N=D[D.length-1];if(!N.t){N.t=K(N,E)}N.t.lastIndex=L;var r=N.t.exec(M);return r?[M.substr(L,r.index-L),r[0],false]:[M.substr(L),\"\",true]}function z(N,r){var L=E.cI?r[0].toLowerCase():r[0];var M=N.k[L];if(M&&M instanceof Array){return M}return false}function F(L,P){L=m(L);if(!P.k){return L}var r=\"\";var O=0;P.lR.lastIndex=0;var M=P.lR.exec(L);while(M){r+=L.substr(O,M.index-O);var N=z(P,M);if(N){x+=N[1];r+='<span class=\"'+N[0]+'\">'+M[0]+\"</span>\"}else{r+=M[0]}O=P.lR.lastIndex;M=P.lR.exec(L)}return r+L.substr(O,L.length-O)}function J(L,M){if(M.sL&&e[M.sL]){var r=d(M.sL,L);x+=r.keyword_count;return r.value}else{return F(L,M)}}function I(M,r){var L=M.cN?'<span class=\"'+M.cN+'\">':\"\";if(M.rB){y+=L;M.buffer=\"\"}else{if(M.eB){y+=m(r)+L;M.buffer=\"\"}else{y+=L;M.buffer=r}}D.push(M);A+=M.r}function G(N,M,Q){var R=D[D.length-1];if(Q){y+=J(R.buffer+N,R);return false}var P=q(M,R);if(P){y+=J(R.buffer+N,R);I(P,M);return P.rB}var L=v(D.length-1,M);if(L){var O=R.cN?\"</span>\":\"\";if(R.rE){y+=J(R.buffer+N,R)+O}else{if(R.eE){y+=J(R.buffer+N,R)+O+m(M)}else{y+=J(R.buffer+N+M,R)+O}}while(L>1){O=D[D.length-2].cN?\"</span>\":\"\";y+=O;L--;D.length--}var r=D[D.length-1];D.length--;D[D.length-1].buffer=\"\";if(r.starts){I(r.starts,\"\")}return R.rE}if(w(M,R)){throw\"Illegal\"}}var E=e[B];var D=[E.dM];var A=0;var x=0;var y=\"\";try{var s,u=0;E.dM.buffer=\"\";do{s=p(C,u);var t=G(s[0],s[1],s[2]);u+=s[0].length;if(!t){u+=s[1].length}}while(!s[2]);if(D.length>1){throw\"Illegal\"}return{r:A,keyword_count:x,value:y}}catch(H){if(H==\"Illegal\"){return{r:0,keyword_count:0,value:m(C)}}else{throw H}}}function g(t){var p={keyword_count:0,r:0,value:m(t)};var r=p;for(var q in e){if(!e.hasOwnProperty(q)){continue}var s=d(q,t);s.language=q;if(s.keyword_count+s.r>r.keyword_count+r.r){r=s}if(s.keyword_count+s.r>p.keyword_count+p.r){r=p;p=s}}if(r.language){p.second_best=r}return p}function i(r,q,p){if(q){r=r.replace(/^((<[^>]+>|\\t)+)/gm,function(t,w,v,u){return w.replace(/\\t/g,q)})}if(p){r=r.replace(/\\n/g,\"<br>\")}return r}function n(t,w,r){var x=h(t,r);var v=a(t);var y,s;if(v){y=d(v,x)}else{return}var q=c(t);if(q.length){s=document.createElement(\"pre\");s.innerHTML=y.value;y.value=k(q,c(s),x)}y.value=i(y.value,w,r);var u=t.className;if(!u.match(\"(\\\\s|^)(language-)?\"+v+\"(\\\\s|$)\")){u=u?(u+\" \"+v):v}if(/MSIE [678]/.test(navigator.userAgent)&&t.tagName==\"CODE\"&&t.parentNode.tagName==\"PRE\"){s=t.parentNode;var p=document.createElement(\"div\");p.innerHTML=\"<pre><code>\"+y.value+\"</code></pre>\";t=p.firstChild.firstChild;p.firstChild.cN=s.cN;s.parentNode.replaceChild(p.firstChild,s)}else{t.innerHTML=y.value}t.className=u;t.result={language:v,kw:y.keyword_count,re:y.r};if(y.second_best){t.second_best={language:y.second_best.language,kw:y.second_best.keyword_count,re:y.second_best.r}}}function o(){if(o.called){return}o.called=true;var r=document.getElementsByTagName(\"pre\");for(var p=0;p<r.length;p++){var q=b(r[p]);if(q){n(q,hljs.tabReplace)}}}function l(){if(window.addEventListener){window.addEventListener(\"DOMContentLoaded\",o,false);window.addEventListener(\"load\",o,false)}else{if(window.attachEvent){window.attachEvent(\"onload\",o)}else{window.onload=o}}}var e={};this.LANGUAGES=e;this.highlight=d;this.highlightAuto=g;this.fixMarkup=i;this.highlightBlock=n;this.initHighlighting=o;this.initHighlightingOnLoad=l;this.IR=\"[a-zA-Z][a-zA-Z0-9_]*\";this.UIR=\"[a-zA-Z_][a-zA-Z0-9_]*\";this.NR=\"\\\\b\\\\d+(\\\\.\\\\d+)?\";this.CNR=\"\\\\b(0[xX][a-fA-F0-9]+|(\\\\d+(\\\\.\\\\d*)?|\\\\.\\\\d+)([eE][-+]?\\\\d+)?)\";this.BNR=\"\\\\b(0b[01]+)\";this.RSR=\"!|!=|!==|%|%=|&|&&|&=|\\\\*|\\\\*=|\\\\+|\\\\+=|,|\\\\.|-|-=|/|/=|:|;|<|<<|<<=|<=|=|==|===|>|>=|>>|>>=|>>>|>>>=|\\\\?|\\\\[|\\\\{|\\\\(|\\\\^|\\\\^=|\\\\||\\\\|=|\\\\|\\\\||~\";this.ER=\"(?![\\\\s\\\\S])\";this.BE={b:\"\\\\\\\\.\",r:0};this.ASM={cN:\"string\",b:\"'\",e:\"'\",i:\"\\\\n\",c:[this.BE],r:0};this.QSM={cN:\"string\",b:'\"',e:'\"',i:\"\\\\n\",c:[this.BE],r:0};this.CLCM={cN:\"comment\",b:\"//\",e:\"$\"};this.CBLCLM={cN:\"comment\",b:\"/\\\\*\",e:\"\\\\*/\"};this.HCM={cN:\"comment\",b:\"#\",e:\"$\"};this.NM={cN:\"number\",b:this.NR,r:0};this.CNM={cN:\"number\",b:this.CNR,r:0};this.BNM={cN:\"number\",b:this.BNR,r:0};this.inherit=function(r,s){var p={};for(var q in r){p[q]=r[q]}if(s){for(var q in s){p[q]=s[q]}}return p}}();hljs.LANGUAGES.cpp=function(){var a={keyword:{\"false\":1,\"int\":1,\"float\":1,\"while\":1,\"private\":1,\"char\":1,\"catch\":1,\"export\":1,virtual:1,operator:2,sizeof:2,dynamic_cast:2,typedef:2,const_cast:2,\"const\":1,struct:1,\"for\":1,static_cast:2,union:1,namespace:1,unsigned:1,\"long\":1,\"throw\":1,\"volatile\":2,\"static\":1,\"protected\":1,bool:1,template:1,mutable:1,\"if\":1,\"public\":1,friend:2,\"do\":1,\"return\":1,\"goto\":1,auto:1,\"void\":2,\"enum\":1,\"else\":1,\"break\":1,\"new\":1,extern:1,using:1,\"true\":1,\"class\":1,asm:1,\"case\":1,typeid:1,\"short\":1,reinterpret_cast:2,\"default\":1,\"double\":1,register:1,explicit:1,signed:1,typename:1,\"try\":1,\"this\":1,\"switch\":1,\"continue\":1,wchar_t:1,inline:1,\"delete\":1,alignof:1,char16_t:1,char32_t:1,constexpr:1,decltype:1,noexcept:1,nullptr:1,static_assert:1,thread_local:1,restrict:1,_Bool:1,complex:1},built_in:{std:1,string:1,cin:1,cout:1,cerr:1,clog:1,stringstream:1,istringstream:1,ostringstream:1,auto_ptr:1,deque:1,list:1,queue:1,stack:1,var:1,map:1,set:1,bitset:1,multiset:1,multimap:1,unordered_set:1,unordered_map:1,unordered_multiset:1,unordered_multimap:1,array:1,shared_ptr:1}};return{dM:{k:a,i:\"</\",c:[hljs.CLCM,hljs.CBLCLM,hljs.QSM,{cN:\"string\",b:\"'\\\\\\\\?.\",e:\"'\",i:\".\"},{cN:\"number\",b:\"\\\\b(\\\\d+(\\\\.\\\\d*)?|\\\\.\\\\d+)(u|U|l|L|ul|UL|f|F)\"},hljs.CNM,{cN:\"preprocessor\",b:\"#\",e:\"$\"},{cN:\"stl_container\",b:\"\\\\b(deque|list|queue|stack|var|map|set|bitset|multiset|multimap|unordered_map|unordered_set|unordered_multiset|unordered_multimap|array)\\\\s*<\",e:\">\",k:a,r:10,c:[\"self\"]}]}}}();hljs.LANGUAGES.r={dM:{c:[hljs.HCM,{cN:\"number\",b:\"\\\\b0[xX][0-9a-fA-F]+[Li]?\\\\b\",e:hljs.IMMEDIATE_RE,r:0},{cN:\"number\",b:\"\\\\b\\\\d+(?:[eE][+\\\\-]?\\\\d*)?L\\\\b\",e:hljs.IMMEDIATE_RE,r:0},{cN:\"number\",b:\"\\\\b\\\\d+\\\\.(?!\\\\d)(?:i\\\\b)?\",e:hljs.IMMEDIATE_RE,r:1},{cN:\"number\",b:\"\\\\b\\\\d+(?:\\\\.\\\\d*)?(?:[eE][+\\\\-]?\\\\d*)?i?\\\\b\",e:hljs.IMMEDIATE_RE,r:0},{cN:\"number\",b:\"\\\\.\\\\d+(?:[eE][+\\\\-]?\\\\d*)?i?\\\\b\",e:hljs.IMMEDIATE_RE,r:1},{cN:\"keyword\",b:\"(?:tryCatch|library|setGeneric|setGroupGeneric)\\\\b\",e:hljs.IMMEDIATE_RE,r:10},{cN:\"keyword\",b:\"\\\\.\\\\.\\\\.\",e:hljs.IMMEDIATE_RE,r:10},{cN:\"keyword\",b:\"\\\\.\\\\.\\\\d+(?![\\\\w.])\",e:hljs.IMMEDIATE_RE,r:10},{cN:\"keyword\",b:\"\\\\b(?:function)\",e:hljs.IMMEDIATE_RE,r:2},{cN:\"keyword\",b:\"(?:if|in|break|next|repeat|else|for|return|switch|while|try|stop|warning|require|attach|detach|source|setMethod|setClass)\\\\b\",e:hljs.IMMEDIATE_RE,r:1},{cN:\"literal\",b:\"(?:NA|NA_integer_|NA_real_|NA_character_|NA_complex_)\\\\b\",e:hljs.IMMEDIATE_RE,r:10},{cN:\"literal\",b:\"(?:NULL|TRUE|FALSE|T|F|Inf|NaN)\\\\b\",e:hljs.IMMEDIATE_RE,r:1},{cN:\"identifier\",b:\"[a-zA-Z.][a-zA-Z0-9._]*\\\\b\",e:hljs.IMMEDIATE_RE,r:0},{cN:\"operator\",b:\"<\\\\-(?!\\\\s*\\\\d)\",e:hljs.IMMEDIATE_RE,r:2},{cN:\"operator\",b:\"\\\\->|<\\\\-\",e:hljs.IMMEDIATE_RE,r:1},{cN:\"operator\",b:\"%%|~\",e:hljs.IMMEDIATE_RE},{cN:\"operator\",b:\">=|<=|==|!=|\\\\|\\\\||&&|=|\\\\+|\\\\-|\\\\*|/|\\\\^|>|<|!|&|\\\\||\\\\$|:\",e:hljs.IMMEDIATE_RE,r:0},{cN:\"operator\",b:\"%\",e:\"%\",i:\"\\\\n\",r:1},{cN:\"identifier\",b:\"`\",e:\"`\",r:0},{cN:\"string\",b:'\"',e:'\"',c:[hljs.BE],r:0},{cN:\"string\",b:\"'\",e:\"'\",c:[hljs.BE],r:0},{cN:\"paren\",b:\"[[({\\\\])}]\",e:hljs.IMMEDIATE_RE,r:0}]}};\n"
            + "hljs.initHighlightingOnLoad();\n"
            + "</script>\n"
            + "<script type=\"text/javascript\" src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS-MML_HTMLorMML\">\n"
            + "</script>\n"
            + "<body>\n"
            + "<p>#BACKLINK#</p>";

    static final String footer = "<br/><p>#BACKLINK#</p></body></html>";
}