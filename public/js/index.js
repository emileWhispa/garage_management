
function Excel() {
    this.dataArray = [];
    this.headerArray = [];
    this.CsvString = "";
    this.fileName = new Date().getTime().toString();

    this.header = function () {
        var csvString = this.CsvString;
        this.headerArray.forEach(function(RowItem) {
            csvString += RowItem + ',';
        });

        csvString += "\r\n";

        this.CsvString = csvString;
    };

    this.data = function () {
        var csvString = this.CsvString;


        this.dataArray.forEach(function(RowItem) {
            RowItem.forEach(function(el) {
                csvString += '"'+ el + '",';
            });
            csvString += "\r\n";
        });

        this.CsvString = csvString;
    };

    this.exportToCsv = function() {

        this.header();

        this.data();

        this.CsvString = "data:application/csv," + encodeURIComponent(this.CsvString);
        var x = document.createElement("A");
        x.setAttribute("href", this.CsvString );
        hide(x);
        x.setAttribute("download",this.fileName+".csv");
        document.body.appendChild(x);
        x.click();
    };

}

function checkValues(json, unSelected, selectedDiv, callback, name, dValue, otherJson) {
    var array = [];

    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var option = document.createElement("div");
        $(option).addClass("form-group");
        var id = el["id"] !== undefined ? el["id"] : 0;

        var input = document.createElement("input");
        input.type = "checkbox";
        input.name = name;
        input.value = id;
        var wrapper = checkWrapper(input);

        itemMove(input, option, unSelected, selectedDiv, el, otherJson);


        var span = document.createElement("span");
        span.innerHTML = defValue(el);

        option.selected = id === dValue;

        option.appendChild(wrapper.parent);
        option.appendChild(span);

        unSelected.appendChild(option);
        array.push(option);

        if (create) callback.onCreate(option, array, el, i);
    });
}

function isCheck( elem ) {
    var valid = elem !== undefined;
    if( !valid ) return false;
    var check = elem["isCheck"];
    var group = elem["grouped"];
    return valid && check !== undefined && check || valid && group !== undefined && group;
}



function jsonAccordion( json ) {
    var accordElem = json.json;
    var array = [];
    Array.prototype.forEach.call(accordElem,function (el,i) {

        var box = document.createElement("div");
        box.className = "panel box";
        var id = "collapse"+i;
        var header = document.createElement("div");
        header.innerHTML = "<h4 class=\"box-title\">\n" +
            "                <a data-toggle=\"collapse\" data-href=\"#\" data-parent=\"#accordion\" href=\"#"+id+"\" aria-expanded=\"false\" class=\"collapsed\">\n" +
            "               "+el["title"]+"\n" +
            "                </a>\n" +
            "              </h4>";
        header.className = "box-header with-border";
        box.appendChild(header);

        array.push(box);

        var body = document.createElement("div");
        body.className = "panel-collapse collapse";
        var place = document.createElement("div");
        place.className = "height-large";
        body.appendChild(place);
        box.appendChild(body);
        body.id = id;

        if( json.callback !== undefined && typeof json.callback === "function" ) json.callback(body,el);

        var classArray = ["box-default", "box-primary", "box-danger", "box-info", "box-warning"];


        var cl = "box-default", pIndex = 0;
        if (classArray[i] === undefined) {
            for (var sp = 0; sp < classArray.length; sp++) {
                if (classArray[pIndex] !== undefined) {
                    cl = classArray[pIndex];
                    pIndex++;
                    break;
                }
                if (sp === classArray.length - 2) pIndex = 0;
            }
        } else
            cl = classArray[i];

        $(box).addClass(cl);

    });

    return {
        boxArray:array,
        put:function (elem) {
            var parent = document.createElement("div");
            parent.id = "accordion";
            parent.className = "box-group";
            iterateJson(this.boxArray,function (el) {
                parent.appendChild(el);
            });
            elem.appendChild(parent);
        }
    }
}


function bigAndSmall() {
    var doc = document.createElement("section");
    doc.className = "row";
    var div1 = document.createElement("div");
    div1.className = "col-md-4 transit";

    var div2 = document.createElement("div");
    div2.className = "col-md-8 transit";

    doc.appendChild(div1);
    doc.appendChild(div2);

    return {
        parent: doc,
        firstDiv: div1,
        secondDiv: div2,
        firstBox: function () {
            $(this.firstDiv).empty();
            var box = document.createElement("div");
            box.className = "box box-solid";

            var headerBox = document.createElement("div");
            headerBox.className = "box-header with-border";
            var h4 = document.createElement("h4");
            h4.className = "box-title";
            headerBox.appendChild(h4);

            var body = document.createElement("div");
            body.className = "box-body";

            var footer = document.createElement("div");
            footer.className = "clearfix box-footer";


            box.appendChild(headerBox);
            box.appendChild(body);

            this.firstDiv.appendChild(box);

            return {
                box: box,
                footer: footer,
                header: headerBox,
                h4: h4,
                body: body,
                bodyEmpty: function () {
                    this.body.innerHTML = "";
                },
                setHeader: function (title) {
                    this.h4.innerHTML = title;
                    return this;
                },
                addFooter: function () {
                    this.box.appendChild(this.footer);
                },
                isLarge: function () {
                    $(this.body).addClass("height-largest");
                }
            };
        },
        secondBox: function () {
            $(this.secondDiv).empty();
            var box = document.createElement("div");
            box.className = "box box-info";

            var headerBox = document.createElement("div");
            headerBox.className = "box-header with-border";
            var h4 = document.createElement("h4");
            h4.className = "box-title";
            headerBox.appendChild(h4);

            var body = document.createElement("div");
            body.className = "box-body";


            box.appendChild(headerBox);
            box.appendChild(body);

            this.secondDiv.appendChild(box);

            return {
                box: box,
                header: headerBox,
                h4: h4,
                body: body,
                setHeader: function (title) {
                    this.h4.innerHTML = title;
                    return this;
                },
                isLarge: function () {
                    $(this.body).addClass("height-largest");
                }
            };
        },
        resize: function () {
            if ($(this.firstDiv).hasClass('col-md-4')) {
                $(this.firstDiv).removeClass("col-md-4").addClass("col-md-1");
                $(this.secondDiv).removeClass("col-md-8").addClass("col-md-11");
            } else {
                $(this.firstDiv).addClass("col-md-4").removeClass("col-md-1");
                $(this.secondDiv).addClass("col-md-8").removeClass("col-md-11");
            }
        }
    }
}


function addCols(cols,json,callback,father,ctrl){
    return List(cols,
        {
            isHidden:false,
            setHidden:function(){
                this.isHidden = !this.isHidden;
            },
            getHidden:function(){
                return this.isHidden;
            },
            onClick:function(a,arr,el,ev){
                if( a === ev.target ) ev.preventDefault();
            },
            onCreate:function (a,array,el) {
                var element = json[el];
                if( typeof element === "object" ){
                    var ft = father !== undefined ? father + "." + el : el;
                    var cols = ctrl.columns(element);
                    var u2 = addCols(cols,element,callback,ft,ctrl);
                    a.parentNode.appendChild(u2);
                    u2.style.marginLeft = "30px";

                    var s = document.createElement("i");
                    s.className = "fa fa-bolt";
                    var elSp = document.createElement("span");
                    elSp.innerHTML = el;
                    elSp.style.marginLeft = "6px";
                    var angle = document.createElement("span");
                    angle.className = "fa fa-angle-down pull-right";

                    a.appendChild(s);
                    a.appendChild(elSp);
                    a.appendChild(angle);

                    var eThis = this;

                    a.parentNode.onclick = function(e){
                        if( e.target !== this && e.target !== a ) return;

                        $(u2).slideToggle();

                        angle.className = eThis.getHidden() ? "fa fa-angle-down pull-right" : "fa fa-angle-right pull-right";

                        eThis.setHidden();
                    };

                    var o = {
                        link:a,
                        bolt:s,
                        name:elSp,
                        angle:angle
                    };

                    if( callback !== undefined && callback.onTree !== undefined && typeof callback.onTree === "function" ){
                        callback.onTree(o,a.parentNode,u2,element,el);
                    }
                }else{
                    a.parentNode.onclick = function(e){
                        e.stopPropagation();
                    };

                    if( callback !== undefined && callback.onMenu !== undefined && typeof callback.onMenu === "function" ){
                        callback.onMenu(a,a.parentNode,element,el,father);
                    }
                }
            }
        });
}


function deepCols(row , el , td ) {
    var cols = of(row).columns();
    iterateJson(cols,function (elem) {
        if( row[el.key] !== undefined){
            expressValue(row[el.key],td);
        }else if( typeof row[elem] === "object" ){
            deepCols(row[elem],el,td);
        }
    });
}


function reportHeader( info , title ) {
    var repHeader = document.createElement("div");
    repHeader.innerHTML = "<div class=\"row no-print\">\n" +
        "           <div class=\"col-xs-12\">\n" +
        "               <a href=\"javascript:window.print();\" class=\"btn btn-primary pull-right\">\n" +
        "                   <i class=\"fa fa-print\"></i> Print report\n" +
        "               </a>\n" +
        "           </div>\n" +
        "       </div>";
    var doc = document.createElement("div");
    doc.className = "row";
    doc.innerHTML = '\n' +
        '<div>\n' +
        '    <table class="table" style="border: none;">\n' +
        '        <thead>\n' +
        '            <tr>\n' +
        '                <td style="border-right: 2px solid #E200E2;vertical-align: middle">\n' +
        '                    <span>\n' +
        '                        <i class="fa fa-circle"></i>\n' +
        '                        <i class="fa fa-circle" style="color: #E200E2;"></i>\n' +
        '                        <i class="fa fa-circle" style="color: #E200E2;"></i>\n' +
        '                    </span>\n' +
        '                </td>\n' +
        '                <td style="vertical-align: top">\n' +
        '                    <span>JALI HOLDING GARAGE MANAGEMENT SYSTEM\n' +
        '                    </span>\n' +
        '                </td>\n' +
        '                <td style="vertical-align: bottom">\n' +
        '                    <small>\n' +
        '                        <em class="text-left">Excellence in data management</em>\n' +
        '                    </small>\n' +
        '                </td>\n' +
        '                <td>\n' +
        '                    <img src="/assets/images/smLogo.png"  alt="" class="img-responsive pull-right" style="width: 50px">\n' +
        '                </td>\n' +
        '            </tr>\n' +
        '        </thead>\n' +
        '    </table>\n' +
        '</div>';

    var eHeader = document.createElement("div");
    eHeader.className = "row";
    eHeader.innerHTML = "\n" +
        "            <div class=\"col-xs-12\">\n" +
        "                <h3>\n" +
        "                    <span class=\"pull-left\">\n" +
        "                        <strong>"+title+"</strong>\n" +
        "                    </span>\n" +
        "                    <span class=\"pull-right\">\n" +
        "                        Date: <small>"+info.date+"</small>\n" +
        "                    </span>\n" +
        "                </h3>\n" +
        "                <div class=\"clearfix\"></div>\n" +
        "                <hr>\n" +
        "\n" +
        "                \n" +
        "            </div>";

    var akka = document.createElement("div");
    akka.appendChild(repHeader);
    akka.appendChild(doc);
    akka.appendChild(eHeader);
    return akka;
}

function expressValue(obj, td) {
    if (typeof obj === "object" && obj && obj.length) {
        var array = [];
        var ul = List(obj,
            {
                onCreate: function (a, array1, el) {
                    var xp  = defValue(el);
                    a.innerHTML = xp;
                    array.push(xp);
                }
            });
        $(ul).addClass("small-li");
        td.appendChild(ul);
        return array;
    } else{
        td.innerHTML = typeof obj === "number" ? obj.toLocaleString() : obj;
        return obj;
    }
}

function tableStack( callback ) {

    var ul = document.createElement("div");
    ul.className = "table-responsive";
    var table = document.createElement("table");
    table.className = "table table-striped";
    var tHead = document.createElement("thead");
    var trH = document.createElement("tr");
    tHead.appendChild(trH);
    var body = document.createElement("tbody");
    table.appendChild(tHead);
    table.appendChild(body);
    ul.appendChild(table);

    var foot = document.createElement("tfoot");
    var ftHr = document.createElement("tr");
    foot.appendChild(ftHr);

    if( callback.onFoot ) table.appendChild(foot);


    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";

    if (create) callback.onCreate(body, trH,tHead,ftHr);

    return ul;
}

function handleReport(div, aLink,ctrl) {
    var section = bigAndSmall();

    var firstBox = section.firstBox();

    firstBox.setHeader("....");
    firstBox.isLarge();


    var loader = ctrl.loader(firstBox.box);

    hoss.ajax({page: aLink.href}, 1, 2, function (res) {
        hide(loader);
        var json = ctrl.jsonParse(res);
        firstBox.setHeader(json.title);
        var o = {
            json: json.nodeList,
            callback: function (div, obj) {
                var c = obj["content"];
                var form = {};
                form.form = c;
                var table = new DesignTable(form, div, true);
                table.callBack = function (e) {
                    handleInReport(e, section.secondBox());
                };
                var el = table.createForm();
                $(div).empty().addClass("five-pad");
                div.appendChild(el);
            }
        };
        var accordion = jsonAccordion(o);
        accordion.put(firstBox.body);
    });

    div.appendChild(section.parent);
}

function handleInReport(res, secondBox , ctrl ) {

        secondBox.isLarge();
        $(secondBox.body).addClass("loading");


            var json = ctrl.jsonParse(res);

            getReport(secondBox,json,ctrl);





}


function getReport( secondBox , json , ctrl ) {
    $(secondBox.body).removeClass("loading");
    secondBox.setHeader("Choose columns to display on report");
    var fields = json.fields;
    var hInfo = json.header;
    var data = json.data;

    var cols = ctrl.columns(fields);

    var array = [];

    var list = addCols(cols, fields,
        {
            onTree: function () {
                //console.log(a);
            },
            onMenu: function (a, li, element, el, ft) {
                var input = document.createElement("input");
                input.type = "checkbox";
                input.value = el;
                console.log(ft);
                var obj = {
                    key: el,
                    father: ft,
                    isN:true,
                    isNumber:function () {
                        return this.isN;
                    },
                    number:0,
                    notNumber:function(){
                        this.isN = false;
                    },
                    reset:function () {
                        this.number = 0;
                    },
                    setNumber:function ( n ) {
                        this.number = typeof n === "number" ? this.number + n : this.number;
                    },
                    value: element
                };
                input.onchange = function () {
                    array.remove(obj);
                    if (this.checked) {
                        array.push(obj);
                    }
                };
                a.removeAttribute("href");
                var sp = document.createElement("div");
                sp.innerHTML = element;
                sp.className = "in-block";
                sp.ondblclick = function () {
                    this.setAttribute("contenteditable", "true");
                    var io = {
                        onHide: function () {
                            a.appendChild(sp);
                            $(sp).removeClass("io-input");
                            sp.removeAttribute("contenteditable");
                        }
                    };
                    $(sp).addClass("io-input");
                    var o = globalWidget(io);
                    o.add(sp);
                };
                sp.onkeyup = function () {
                    obj.value = this.textContent;
                    if (input.checked) {
                        array.remove(obj);
                        array.push(obj);
                    }
                };
                sp.style.paddingLeft = "10px";

                a.appendChild(input);
                a.appendChild(sp);

            }
        },undefined,ctrl);

    secondBox.body.appendChild(list);

    var v = {
        value : 1,
        setValue:function (v) {
            this.value = v;
        }
    };

    function preViewReport() {


        var tGroup = document.createElement("div");
        tGroup.className = "form-group";
        var t = document.createElement("textarea");
        t.className = "form-control";
        t.placeholder = "Enter report title";
        tGroup.appendChild(t);
        var div = document.createElement("div");
        var formGroup = document.createElement("div");
        formGroup.className = "form-group";
        var formGroup2 = document.createElement("div");
        formGroup2.className = "form-group";
        var formGroup3 = document.createElement("div");
        formGroup3.className = "form-group";
        var inp = radioWrapper("View report");
        var inp2 = radioWrapper("Export report to excel");
        var inp3 = radioWrapper("View report && Export to excel");
        inp.setName("realN");
        inp.setName("realN");
        inp.setName("realN");
        div.appendChild(tGroup);
        formGroup.appendChild(inp.parent);
        div.appendChild(formGroup);
        formGroup2.appendChild(inp2.parent);
        div.appendChild(formGroup2);
        formGroup3.appendChild(inp3.parent);
        div.appendChild(formGroup3);

        inp.input.checked = true;
        v.setValue(1);

        inp.input.onchange = function () {
            v.setValue(1);
        };

        inp2.input.onchange = function () {
            v.setValue(2);
        };

        inp3.input.onchange = function () {
            v.setValue(3);
        };



        confirmBox({
            title: "Enter report title",
            body: div,
            accept: function (e, wig) {

                if(!t.value.trim()) {
                    t.focus();
                    return;
                }

                modalClose();

                viewReport(t.value);

                hide(wig);

            }
        });
    }

    function viewReport(title) {

        var tab,body;
        if( v.value !== 2 ) {
            tab = newTab();
            body = tab.document.body;
        }else{
            body = document.createElement("div");
        }

        var container = document.createElement("section");
        container.className = "invoice";
        body.appendChild(container);

        container.appendChild(reportHeader(hInfo, title));

        var csvArray = [];
        var csvHeader = [];
        var tb = tableStack({
            onFoot:true,
            onCreate: function (body, head,tHead,foot) {
                $(body).addClass("table-14");
                stackTr(array,
                    {
                        onCreate: function (td, tr, el) {
                            td.remove();
                            var th = document.createElement("th");
                            th.textContent = el.value;
                            head.appendChild(th);
                            csvHeader.push(el.value);
                        }
                    });

                stackRow(data,
                    {
                        onCreate: function (trH, elRow) {

                            var cSub = [];
                            stackTr(array,
                                {
                                    onCreate: function (td, tr, el) {
                                        var o = elRow[el.key];
                                        var def = o !== undefined && el.father === undefined;

                                        var fValue;

                                        if (def) {
                                            fValue = expressValue(o, td);
                                        } else fValue = deepValues(elRow, el, td);

                                        if( typeof fValue === "number" && el.isNumber() ) {
                                            el.setNumber(fValue);
                                        }else el.notNumber();

                                        trH.appendChild(td);

                                        fValue = typeof fValue === "number" ? fValue.toLocaleString() : fValue;

                                        cSub.push(fValue);
                                    }

                                });
                            csvArray.push(cSub);
                            body.appendChild(trH);
                        }
                    });


                stackTr(array,
                    {
                        onCreate: function (td, tr, el) {
                            td.remove();
                            var th = document.createElement("th");
                            th.textContent = el.isNumber() && el.number ? el.number.toLocaleString() : el.value;
                            el.reset();
                            foot.appendChild(th);
                        }
                    });

                var excel = new Excel();
                excel.headerArray = csvHeader;
                excel.dataArray = csvArray;
                excel.fileName = title;

                if( v.value === 2 || v.value === 3 ) excel.exportToCsv();

            }
        });



        container.appendChild(tb);
    }

    var reportButton = buttonDefault("View Report");
    reportButton.onclick = function () {
        preViewReport();
    };

    var n = reportButton.cloneNode(true);
    $(n).addClass("pull-right");
    n.onclick = function () {
        preViewReport();
    };

    secondBox.header.appendChild(n);

    $(reportButton).addClass("btn-block");

    secondBox.body.appendChild(reportButton);

}



function globalWidget( o ) {

    var doc = document.createElement("div");

    doc.className = "super-widget global-widget";

    var ing = document.createElement("div");
    ing.className = "global-center";

    doc.appendChild(ing);

    var bAppender = document.createElement("div");
    bAppender.className = "wiger";

    ing.appendChild(bAppender);

    bAppender.onclick = function (ev) {
        if( ev.target === this){
            $(doc).fadeOut();
            if( o && typeof o.onHide === "function" ) o.onHide(this,doc);
            modalClose();
        }
    };

    var box = document.createElement("div");
    box.className = "boxed-widget-io col-md-5 row col-centered transit";
    bAppender.appendChild(box);

    document.body.appendChild(doc);

    modalOpen();

    "use strict";

    return {
        parent:doc,
        box:box,
        remove:function () {
            doc.remove();
            modalClose();
        },
        hideModal:function () {
            hide(doc);
            modalClose();
        },
        showModal:function () {
            show(doc);
            modalOpen();
        },
        add:function (elem) {
            this.box.appendChild(elem);
            elem.focus();
        },
        extend:function () {
            $(this.box).removeClass("col-md-5").addClass("col-md-10");
        }
    }
}

function handleExtraReport(div, aLink , ctrl ) {

    $(div).empty();

    var section = bigAndSmall();

    var firstBox = section.firstBox();

    firstBox.setHeader("....");
    firstBox.isLarge();


    var loader = ctrl.loader(firstBox.box);

    ctrl.request({
        url: aLink.href,
        success:function (res) {
            hide(loader);
            var json = ctrl.jsonParse(res);

            var pad = document.createElement("div");
            pad.className = "ten-pad";
            var io = globalWidget();
            var newPanel = document.createElement("div");
            newPanel.className = "col-md-8 height-large text-left";
            var ul = List(json,
                {
                    onCreate: function (a, array, el) {
                        var inp = input("radio", "report");
                        inp.onchange = function () {
                            var loaderL = ctrl.loader(pad);
                            hoss.ajax({page: el.href}, 1, 2, function (res) {
                                hide(loaderL);
                                $(pad).addClass("col-md-4");
                                io.extend();
                                io.add(newPanel);
                                var big = bigAndSmall();
                                big.resize();
                                $(newPanel).empty();
                                var secondB = big.secondBox();
                                var vValue = el.value;
                                var tint = vValue;
                                secondB.setHeader("Choose what use while filtering <b class='text-red'>" + tint + "</b>");
                                secondB.isLarge();
                                newPanel.appendChild(secondB.box);

                                var js = ctrl.jsonParse(res);

                                var chosenArray = [];

                                function createList(jsCols, title) {

                                    var h1 = document.createElement("h2");
                                    h1.className = "text-bold";
                                    h1.innerHTML = title;

                                    var c = ctrl.columns(jsCols);
                                    var lst = addCols(c,jsCols,
                                        {
                                            onMenu:function(a,li){
                                                li.remove();
                                            },
                                            onTree:function (o,li,ul,el) {
                                                if( !el.access ) return;
                                                o.link.innerHTML = "";
                                                var ch = input("checkbox", "column");

                                                var i2 = input("hidden", el.key, el.store);
                                                var i3 = input("hidden", el.key, el.value);
                                                var i4 = input("hidden", el.key, el.extra);

                                                var isExtra = el.extra !== undefined && el.extra;

                                                ch.onchange = function () {
                                                    chosenArray.remove(i4);
                                                    chosenArray.remove(i3);
                                                    chosenArray.remove(i2);
                                                    if (this.checked) {
                                                        chosenArray.push(i3);
                                                        chosenArray.push(i2);
                                                        if( isExtra ){
                                                            chosenArray.push(i4);
                                                        }
                                                    }
                                                };
                                                var checkB = checkWrapper(ch);
                                                checkB.onclick = function(ex){
                                                    ex.stopPropagation();
                                                };
                                                o.link.appendChild(checkB.parent);
                                                var s = document.createElement("span");
                                                s.innerHTML = el.value;
                                                o.link.appendChild(s);
                                            }
                                        },undefined,ctrl);


                                    $(lst).addClass("margin-top-bottom");

                                    var div = document.createElement("div");

                                    div.appendChild(h1);
                                    div.appendChild(lst);

                                    return div;
                                }

                                var list2 = createList(js.columns, "Choose on list");

                                var ripple = rippleButton("View Final Step");
                                var loader = ctrl.loader(secondB.body);
                                hide(loader);
                                ripple.onclick = function () {
                                    var form = document.createElement("form");
                                    form.method = "POST";
                                    form.action = js.route;
                                    iterateJson(chosenArray, function (el) {
                                        form.appendChild(el);
                                    });
                                    show(loader);

                                    ctrl.saveForm({
                                        form:form,
                                        success:function (res) {
                                            hide(loader);
                                            io.hideModal();

                                            var cAgain = rippleButton("<i class='fa fa-refresh'></i> Choose again");
                                            cAgain.onclick = function () {
                                                io.showModal();
                                            };

                                            var c = ctrl.jsonParse(res);

                                            var form = {};
                                            form.form = c.content;
                                            var table = new DesignTable(form, firstBox.body, true);
                                            table.callBack = function (form) {
                                                handleInReport(form, section.secondBox());
                                            };
                                            var ex = table.createForm();
                                            $(ex).addClass("margin-top");
                                            firstBox.bodyEmpty();
                                            firstBox.body.appendChild(cAgain);
                                            firstBox.body.appendChild(ex);
                                            firstBox.setHeader(tint);
                                        }
                                    });
                                };
                                secondB.body.appendChild(ripple);

                                var otherList = document.createElement("div");

                                var btx = buttonDefault("<i class='fa fa-cart-arrow-down'></i> more options");
                                $(btx).addClass("btn-block");
                                btx.onclick = function () {
                                    show(loader);
                                    hoss.ajax({page: js.nextRoute}, 1, 2, function (res) {
                                        hide(loader);
                                        var json = ctrl.jsonParse(res);
                                        var l2 = createList(json, vValue + " Belong in");
                                        $(otherList).empty();
                                        otherList.appendChild(l2);
                                    });
                                };

                                secondB.body.appendChild(list2);
                                secondB.body.appendChild(otherList);
                                //secondB.body.appendChild(btx);

                            });
                        };
                        var i = radioWrapper(inp, el.value);
                        a.appendChild(i.parent);
                    }
                });
            $(ul).addClass("text-left");
            pad.appendChild(ul);
            io.add(pad);
        }
    });

    div.appendChild(section.parent);
}

function rippleButton(title) {
    var btn = document.createElement("button");
    btn.className = "ripple pen-button bg-purple full-width";
    btn.innerHTML = title;
    return btn;
}

function buttonDefault(title) {
    var b = document.createElement("button");
    b.className = "btn btn-default";
    b.type = "button";
    b.innerHTML = "<i class='fa fa-gavel'></i> " + title;
    return b;
}