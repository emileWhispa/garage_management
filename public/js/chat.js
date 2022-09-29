function Chat() {
    this.webSocketUrl = "";
    this.user = {};
    this.ws = null;
    this.div = document.createElement("div");

    this.createButton = function () {
        var a = document.createElement("button");
        a.className = "floating-button bg-success super-shadow";
        a.innerHTML = "<i class='fa fa-envelope-o'></i>";
        var div = this.div;
        div.className = "chat-div bg-light super-shadow";
        document.body.appendChild(div);
        document.body.addEventListener("click", function () {
            //$(div).effect("drop");
            $(div).hide();
        });
        div.onclick = function (ev) {
            ev.stopPropagation();
        };

        var ws = this.ws;

        var text = document.createElement("textarea");
        text.placeholder = "Chat here ....";
        text.className = "type-text";
        div.appendChild(text);
        var user = this.user;

        text.onkeyup = function () {
            var value = this.value;
            ws.send(JSON.stringify({
                symbol: value,
                value: new Date().getTime(),
                type: "typing",
                user: user
            }));
        };

        a.onclick = function (ev) {
            ev.stopPropagation();
            $(div).show().effect("bounce");
        };
        document.body.appendChild(a);
    };

    this.start = function () {
        var ws = new WebSocket(this.webSocketUrl);
        this.ws = ws;
        var div = this.div;
        var data = document.createElement("div");
        data.className = "chat-msg";
        div.appendChild(data);
        ws.onmessage = function (event) {
            var message;
            message = JSON.parse(event.data);

            var m = document.createElement("div");
            var l = document.createElement("a");
            l.href = "#";
            l.textContent = message.user.email;

            m.innerHTML = message.content;

            var x = document.createElement("div");
            x.appendChild(l);
            x.appendChild(m);
            data.appendChild(x);

        };
        this.createButton();
    }
}


function searchJson(source, col, name) {
    return searchJsonArrayCol(source, [col], name);
}

function searchJsonArrayCol(source, cols, name) {
    var results;

    name = name.toString().toUpperCase();
    results = source.filter(function (entry) {
        var isReturn = false;
        Array.prototype.forEach.call(cols, function (col) {
            var a = entry[col] !== undefined && entry[col] !== null && entry[col].toString().toUpperCase().indexOf(name) !== -1;
            if (!isReturn && a) isReturn = true;
        });

        return isReturn;
    });
    return results;
}

function searchJsonAllCol(source, name) {
    var a = new Controller();
    var cols = a.columns(source);
    return searchJsonArrayCol(source, cols, name);
}


function defValue(el) {
    el = typeof el === "object" && el[0] !== undefined ? el[0] : el;
    return typeof el !== "object" ? el : el["print"] !== undefined ? el["print"] : el["name"] !== undefined ? el["name"] : el;
}

function stackSelectWith(json, callback, name, dValue, xName, search, isN) {
    var a = stackSelect(json, callback, name, dValue, xName, search, isN);
    var grp = document.createElement("div");
    grp.className = "form-group";
    var lbl = document.createElement("label");
    lbl.textContent = xName;
    grp.appendChild(lbl);
    grp.appendChild(a);

    return {
        parent: grp,
        label: lbl,
        input: a
    }
}


function stackSelect(json, callback, name, dValue, xName, search, isN) {
    var select = document.createElement("select");
    select.className = "form-control";
    select.name = name;
    var defaultValue = document.createElement("option");
    defaultValue.value = "";
    defaultValue.innerHTML = "-- Select from list here --";
    select.appendChild(defaultValue);
    if (xName !== undefined) $(select).attr("data-name", xName);
    var array = [];
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var valueHtml = defValue(el);
        var option = document.createElement("option");
        var id = el["id"] !== undefined ? el["id"] : 0;
        option.value = id.toString();
        option.innerHTML = valueHtml;

        option.selected = id === dValue;

        select.appendChild(option);
        array.push(option);

        if (create) callback.onCreate(option, array, el, i);
    });
    return isN ? select : modalSelect({
        title: xName,
        data: json,
        name: name,
        dValue: dValue,
        search: search
    });
}

function modalSelect(opt) {
    var div = document.createElement("div");
    div.className = "modal-select pointer relative";
    var span = document.createElement("span");
    span.textContent = opt.title;
    span.name = "uri";
    div.tabIndex = 0;
    span.className = "selected-span";
    div.appendChild(span);
    var caret = document.createElement("span");
    caret.className = "caret";

    var list = document.createElement("div");
    list.className = "select-list hidden full-width";

    var valueInput = document.createElement("input");
    valueInput.className = "hidden";
    $(valueInput).attr("data-name", opt.title);
    valueInput.name = opt.name;
    valueInput.type = "text";
    valueInput.oninput = function () {
        console.log(this.value);
    };

    div.appendChild(valueInput);


    var divInput = document.createElement("div");
    divInput.className = "five-pad";

    var input = document.createElement("input");
    input.className = "form-control input-sm";
    input.placeholder = "Search ...";
    input.setAttribute("data-escape", "1");
    divInput.appendChild(input);

    var ul = document.createElement("ul");
    ul.className = "select-ul";

    list.appendChild(divInput);

    var innerList = document.createElement("div");
    innerList.className = "in-list";

    list.appendChild(innerList);
    innerList.appendChild(ul);

    div.onclick = function () {
        if ($(list).hasClass("hidden")) {
            $(list).removeClass("hidden")
        } else {
            $(list).addClass("hidden")
        }
    };

    document.addEventListener("click", function (e) {
        if (e.target !== div && e.target !== span) $(list).addClass("hidden");
    });

    list.onclick = function (ev) {
        ev.stopPropagation();
    };

    var array = [];
    var index = 0;
    var dValue = opt.dValue;

    function makeChange(indx, value, text, li) {
        span.innerHTML = text;
        valueInput.value = value;
        $(array).removeClass("chosen");
        $(li).addClass("chosen");
        dValue = value;
        index = indx;
    }

    function getData(data, dValue) {
        array = [];
        ul.innerHTML = "";
        Array.prototype.forEach.call(data, function (el, k) {
            var li = document.createElement("li");
            var value = defValue(el);
            li.innerHTML = value;
            li.tabIndex = 0;
            li.onclick = function () {
                makeChange(k, el.id, value, this);
                $(list).addClass("hidden");
            };
            li.onfocus = function () {
                makeChange(k, el.id, value, this);
            };

            if (el.id === dValue) makeChange(k, el.id, value, li);

            array.push(li);
            ul.appendChild(li);
        });

    }

    getData(opt.data, dValue);

    var request = null;
    var backUp = opt.data;

    var c = new Controller();

    input.onkeyup = function () {
        if (opt.search && this.value) {
            var o = this;
            $(o).addClass("imgLoads");
            var search = opt.search.replace("~q", encodeURIComponent(o.value));
            if (request !== null) {
                request.abort();
            }
            request = c.request({
                url: search,
                success: function (res) {
                    var data = c.jsonParse(res);
                    getData(data, dValue);
                    $(o).removeClass("imgLoads");
                }
            });
        } else if (opt.search) {
            getData(backUp, dValue);
        } else {
            var data = searchJsonArrayCol(opt.data, ["name", "print"], this.value);
            getData(data, dValue);
        }
    };


    var obj;
    div.onkeydown = function (ev) {
        ev.stopPropagation();

        if (ev.code === "ArrowDown" || ev.code === "ArrowUp") ev.preventDefault();
    };
    div.onkeyup = function (ev) {
        ev.stopPropagation();
        if (ev.code === "ArrowDown") {
            index = array.length > index - 2 ? ++index : index;
            obj = array[index];

            if (!obj) {
                --index;
                return;
            }

            if ($(list).hasClass("hidden")) {
                obj.click();
            } else {
                obj.focus();
            }
        } else if (ev.code === "ArrowUp") {
            index = array.length > index - 1 ? --index : index;

            obj = array[index];

            if (!obj) {
                ++index;
                return;
            }

            if ($(list).hasClass("hidden")) {
                obj.click();
            } else {
                obj.focus();
            }
        } else if (ev.code === "Enter") {
            obj = array[index];

            if (!obj) return;

            obj.click();
        }
    };


    div.appendChild(list);


    div.appendChild(caret);

    setTimeout(function () {

        valueInput.form.addEventListener("reset", function () {
            span.innerHTML = opt.title;
        });
    }, 2000);

    return div;
}


function confirmBox(object) {
    var widget = document.querySelector(".light-widget");
    var divBox = (widget !== null) ? widget.querySelector(".boxed-widget") : document.createElement("div");
    modalOpen();
    $(divBox).addClass(object['extended'] ? "col-md-10" : "col-md-5");
    $(divBox).removeClass(object['extended'] ? "col-md-5" : "col-md-10");
    if (widget) {
        jsFadeIn(widget, "table");
    } else {
        widget = document.createElement("div");
        var boxAppender = document.createElement("div");
        boxAppender.appendChild(divBox);
        widget.appendChild(boxAppender);
        boxAppender.onclick = function (e) {
            if (e.target !== this) return;
            $(widget).fadeOut(300);
            var cancel = this.querySelector(".cancel-button");
            if (cancel) cancel.click();
        };
        var tools = document.createElement("div");
        var he = document.createElement("div");
        var bo = document.createElement("div");
        divBox.appendChild(he);
        divBox.appendChild(bo);
        divBox.appendChild(tools);
        document.body.appendChild(widget);
        $(divBox).addClass("boxed-widget col-centered super-shadow");
        $(boxAppender).addClass("wiger");
        $(he).addClass("header");
        $(bo).addClass("body").addClass("relative");
        $(tools).addClass("top-line").addClass("text-right");
        $(widget).addClass("fixedPosition").addClass("light-widget");
    }

    displayButton(object, divBox, widget);
}


function displayButton(object, tools, wig) {
    var tool = tools.querySelector(".top-line");
    var body = tools.querySelector(".body");
    var header = tools.querySelector(".header");
    var hed = "<i class='fa fa-home text-green big-font right-pad'></i>";
    body.innerHTML = "";
    if (object.body !== undefined && typeof object.body === "object") body.appendChild(object.body);
    else if (object.message !== undefined) body.innerHTML = object.message;

    if (object.title !== undefined) header.innerHTML = hed + object.title;
    tool.innerHTML = "";
    var okButton = document.createElement("button");
    okButton.innerHTML = object.button === undefined ? "Ok" : object.button;
    okButton.className = "btn btn-success";
    okButton.onclick = function (e) {
        if (object.accept !== undefined && typeof  object.accept === "function") object.accept(e, wig, this, body, tools);
    };
    tool.appendChild(okButton);
    var cancelButton = document.createElement("button");
    cancelButton.innerHTML = "Cancel";
    cancelButton.className = "btn btn-danger cancel-button";
    cancelButton.onclick = function (e) {
        hide(wig);
        modalClose();
        if (object.cancel !== undefined && typeof  object.cancel === "function") object.cancel(e, wig, this, body, tools);
    };
    tool.appendChild(cancelButton);
    okButton.focus();
    tools.style.paddingBottom = tool.offsetHeight + "px";

    if (object.create !== undefined && typeof  object.create === "function") object.create(wig, body);
}


function modalOpen() {
    $(document.body).addClass("modal-open");
}

function modalClose() {
    $(document.body).removeClass("modal-open");
}


function jsFadeIn(el, type) {
    el.style.display = (type !== undefined) ? type : "block";
    el.style.opacity = 0;

    var last = +new Date();
    var tick = function () {
        el.style.opacity = +el.style.opacity + (new Date() - last) / 600;
        last = +new Date();

        if (+el.style.opacity < 1) {
            (window.requestAnimationFrame && requestAnimationFrame(tick)) || setTimeout(tick, 16);
        }
    };

    tick();
}


function alertBody(elem, msg, type, c) {
    var page = elem.href !== undefined ? elem.href : elem.value;
    c = c !== undefined ? c : new Controller();
    confirmBox({
        title: "Message alert",
        body: msg,
        accept: function (e, wig, but, body, box) {
            var l = c.loader(box);
            l.show();
            c.request({
                url: page,
                success: function () {
                    l.hide();
                }
            })
        }
    });
    return false;
}

function spareApprove(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("Approve spare parts requests.");
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            l.hide();
            var json = ctrl.jsonParse(res);

            var cArray = [];
            var gManagerComment = '', tMdComment = '', procurementComment = '', financeComment = '';
            var ul = List(json.data,
                {
                    onCreate: function (a, li, el) {
                        var wr = checkWrapper(el.print);
                        a.appendChild(wr.parent);
                        var input = document.createElement("input");
                        input.type = "hidden";
                        input.value = el['price'];
                        input.name = el.unique;
                        var qua = document.createElement("input");
                        qua.type = "hidden";
                        qua.value = el['quantity'];
                        qua.name = el.unique;
                        a.appendChild(input);
                        a.appendChild(qua);
                        var t = textarea(true);
                        t.setLabel("Modify price");
                        t.setValue(input.value);
                        var t2 = textarea(true);
                        t2.isNumber();
                        t.isNumber();

                        if( json.disabled ){
                            t2.readOnly();
                            t.readOnly();
                        }

                        var div = document.createElement("div");

                        var arr = [];
                        t.hide();
                        if (json.procurement) {
                            var s = select();
                            s.add([
                                {
                                    value:"",
                                    text:"-- Select here --"
                                },
                                {
                                    value:true,
                                    text:"On contract"
                                },
                                {
                                    value:false,
                                    text:"Not contract"
                                }
                            ]);
                            s.select.onchange = function(){
                                if( this.value === "true" ){
                                    t.show();
                                    t.readOnly();
                                    t.setValue(el.spare.price);
                                }else if( this.value === "false" ) {
                                    t.show();
                                    t.notReadOnly();
                                    t.setValue(null);
                                }
                            };


                            div.appendChild(s.parent);
                            arr.push({field:s.select});
                        }
                        div.appendChild(t.parent);
                        div.appendChild(t2.parent);
                        t2.setLabel("Modify quantity");
                        t2.setValue(qua.value);
                        arr.push({field:t.input});
                        arr.push({field:t2.input});


                        wr.input.onchange = function () {
                            if (this.checked) {
                                cArray.push(el);
                                confirmBox({
                                    title: "Add price",
                                    body: div,
                                    accept: function (e, wig) {
                                        ctrl.validate(arr,function () {
                                            hide(wig);
                                            input.value = t.input.value;
                                            qua.value = t2.input.value;
                                        });
                                    },
                                    cancel:function () {
                                        wr.input.checked = false;
                                    }
                                })
                            }else{
                                cArray.remove(el);
                            }
                        };
                        wr.setName(el.unique);
                        wr.setValue(el.id);

                        gManagerComment = el['garageMComment'];
                        tMdComment = el['directorComment'];
                        procurementComment = el['procurementComment'];
                        financeComment = el['financeComment'];
                    }
                });

            var col = ctrl.centerCol();

            col.fixFooter();

            var form = document.createElement("form");

            form.appendChild(ul);
            form.method = "POST";
            form.action = json.save;
            ctrl.tokenF(form, json.tokenName, json.tokenValue);

            var bu = button("Approve selected");

            var place = document.createElement("div");

            if (ctrl.isObject(json.transport)) {
                place.appendChild(ctrl.displayB(gManagerComment, "Garage manager"));
            }

            if (ctrl.isObject(json.procurement)) {
                place.appendChild(ctrl.displayB(gManagerComment, "Garage manager"));
                place.appendChild(ctrl.displayB(tMdComment, "Transport managing director"));
                place.appendChild(ctrl.displayB(financeComment, "Finance( CFO ) manager"));
            }

            if (ctrl.isObject(json.finance)) {
                place.appendChild(ctrl.displayB(gManagerComment, "Garage manager"));
                place.appendChild(ctrl.displayB(tMdComment, "Transport managing director"));
            }


            var tArea = textarea();
            tArea.setName("comment");
            tArea.setHolderAndLabel("Add comment on this approval");
           place.appendChild(tArea.parent);


            var rType = select();

            rType.add(
                [
                    {
                        value:"",
                        text:"-- Select report type --"
                    },
                    {
                        value:1,
                        text:"-- Purchasing order --"
                    },
                    {
                        value:2,
                        text:"-- Purchasing voucher --"
                    }
                ]
            );

           //  place.appendChild(rType.parent);

           //place.innerHTML = "";

            bu.block();
            bu.parent.onclick = function () {
                confirmBox({
                    title: "Approve",
                    body: place,
                    button:"Approve",
                    accept: function (e, wig) {
                        hide(wig);
                        var ld = ctrl.loader(col.box);
                        ld.show();
                        hide(this.body);
                        form.appendChild(this.body);
                        ctrl.saveForm({
                            form: form,
                            success: function (res) {
                                var obj = ctrl.jsonParse(res);
                                cArray = obj.list;
                                var date = obj.object.date;
                                ld.hide();
                                o.click();

                                if (ctrl.isObject(json.procurement)) {
                                    confirmBox({
                                        title: "Choose report type",
                                        body: rType.parent,
                                        accept: function () {

                                            if (ctrl.isObject(json.procurement)) {
                                                var tab = newTab();
                                                var body = tab.document.body;

                                                var container = document.createElement("section");
                                                container.className = "invoice";
                                                var h = document.createElement("h2");
                                                var ttt = rType.select.value === "2" ? "voucher" : "order";
                                                ttt = "purchasing " + ttt;
                                                h.innerText = ttt.toUpperCase();
                                                // container.appendChild(reportHeader("JALI","GARAGE MANAGEMENT SYSTEM"));
                                                container.appendChild(h);
                                                body.appendChild(container);

                                                if( rType.select.value === "2" ){
                                                    var dT = document.createElement("div");
                                                    dT.innerHTML = "<div class=\"table-responsive\"><table class=\"table table-striped table-14\"><thead><tr><th>Date of transmission</th><th>From</th><th>Through</th><th>To</th><th>For</th></tr></thead><tbody><tr><td>"+date+"</td><td><span contenteditable='true'>Logistic officer J.T.L</span></td><td contenteditable='true'><span contenteditable='true'>Procurement officer</span></td><td><span contenteditable='true'>COO JHL</span></td><td>Signature</td></tr></tbody></table></div> <br>. <br><span contenteditable='true'>Dear Chief of operation. (COO).</span> <br><span contenteditable='true'>We have attached a purchase order to FLOSAM Ltd supplier for your approval & signature; this is an order equivalent to "+obj.sum+" Frw for spare parts fix in J.H vehicles. the quantity and quality required are as indicated on the purchase order attached.</span> <br>Thanks <br><br>";


                                                    container.appendChild(dT);
                                                    var ix = document.createElement("div");
                                                    if (ctrl.isObject(json.procurement) && rType.select.value === "2") {
                                                        ix.appendChild(ctrl.displayB(gManagerComment, "Garage manager"));
                                                        ix.appendChild(ctrl.displayB(tMdComment, "Transport managing director"));
                                                        ix.appendChild(ctrl.displayB(financeComment, "Finance( CFO ) manager"));
                                                        ix.appendChild(ctrl.displayB(tArea.input.value, "Procurement manager"));

                                                        container.appendChild(ix);

                                                    }


                                                }else {

                                                    var cols = [
                                                        {
                                                            key: "spareName",
                                                            display: "Spare part name"
                                                        },
                                                        {
                                                            key: "quantity",
                                                            display: "Quantity"
                                                        },
                                                        {
                                                            key: "price",
                                                            display: "Amount/Item"
                                                        },
                                                        {
                                                            key: "total",
                                                            display: "Total amount"
                                                        },
                                                        {
                                                            key: "vat",
                                                            display: "VAT( 18% )"
                                                        },
                                                        {
                                                            key: "finalTotal",
                                                            display: "Total price"
                                                        }
                                                    ];


                                                    var tb = tableStack(
                                                        {
                                                            onCreate: function (body, head) {
                                                                $(body.parentNode).addClass("table-14");

                                                                stackTr(cols,
                                                                    {
                                                                        onCreate: function (td, tr, el) {
                                                                            var th = document.createElement("th");
                                                                            th.innerHTML = el.display;
                                                                            head.appendChild(th);
                                                                        }
                                                                    });

                                                                stackRow(cArray,
                                                                    {
                                                                        onCreate: function (tr, el) {

                                                                            var tx = stackTr(cols,
                                                                                {
                                                                                    onCreate: function (td, trX, elem) {
                                                                                        td.innerHTML = el[elem.key];
                                                                                    }
                                                                                });

                                                                            body.appendChild(tx);
                                                                        }
                                                                    });
                                                            }
                                                        });
                                                    var diver = document.createElement("div");

                                                    diver.innerHTML = "<b>To</b> : <span contenteditable='true'>FLOSAM LTD</span><br><b>P.O BOX</b> : <span contenteditable='true'>701</span><br><b>Email</b> : <span contenteditable='true'>Flosam@gmail.com</span><br><b>Tin</b> : <span contenteditable='true'>102043891</span><br><br> Dear sir/Madam. <br><br><span contenteditable='true'> We are pleased to place an order for the following spare parts </span><br>";

                                                    container.appendChild(diver);
                                                    container.appendChild(tb);

                                                    var dx = document.createElement("div");
                                                    dx.innerHTML = "<br> <span contenteditable='true'>Please notify us immediately if you are unable to provide above specified</span> <br> <b>Executive chairman</b><br>";

                                                    container.appendChild(dx);

                                                    var dip = document.createElement("div");



                                                    container.appendChild(dip);


                                                    var div = document.createElement("div");
                                                    div.className = "margin-top";
                                                    div.innerHTML = "Signature";
                                                    var d2 = document.createElement("div");
                                                    d2.className = "margin-top";
                                                    container.appendChild(div);
                                                    container.appendChild(d2);

                                                }


                                            }
                                        }
                                    });
                                }
                            }
                        });
                    }
                });


            };

            col.body.appendChild(form);
            col.footer.appendChild(bu.parent);

            o.data.appendChild(col.parent);
        }
    });
}

function stockSummary(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("View stock summary.");
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            var json = ctrl.jsonParse(res);
            l.hide();
            $(o.data).addClass("bg-light five-pad height-large relative").empty();
            var dLoader = ctrl.loader(o.data);

            var tb = tableStack(
                {
                    onCreate:function (body,head) {
                        stackTr(json.columns,
                            {
                                onCreate:function (td,tr,el) {
                                    var th = document.createElement("th");
                                    th.innerHTML = el.title;
                                    head.appendChild(th);
                                }
                            });


                        stackRow(json.data,
                            {
                                onCreate:function (tr,elem) {

                                    stackTr(json.columns,
                                        {
                                            onCreate:function (td,t,el) {
                                                td.innerHTML = elem[el.key];
                                                tr.appendChild(td);
                                            },
                                            onClick:function () {
                                                var body = document.createElement("div");
                                                dLoader.show();
                                                ctrl.request({
                                                    url:elem.route,
                                                    success:function (res) {
                                                        dLoader.hide();
                                                        var json = ctrl.jsonParse(res);
                                                        var list = List(json,
                                                            {
                                                                onCreate:function (a,o,el) {
                                                                    a.innerHTML = el.print;
                                                                }
                                                            });
                                                        body.appendChild(list);
                                                        confirmBox({
                                                            title:"Home",
                                                            body:body
                                                        })
                                                    }
                                                });


                                            }
                                        });

                                    $(tr).addClass("pointer");
                                    body.appendChild(tr);
                                }
                            });
                    }
                }
            );

            o.data.appendChild(tb);
        }
    });
}
function profileUpdate(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("View stock summary.");
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            var json = ctrl.jsonParse(res);
            l.hide();

            var form = {};
            form.form = json;
            var table = new DesignTable(form, o.data, true);
            table.callBack = function (e) {
                o.click(e);
            };
            var el = table.createForm();

            $(el).addClass("col-md-4");

            $(o.data).empty().append(el);
        }
    });
}

function editProf(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("Edit system profile information.");
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            var json = ctrl.jsonParse(res);
            l.hide();

            var form = {};
            form.form = json;
            var table = new DesignTable(form, o.data, true);
            table.callBack = function (e) {
                o.click(e);
            };
            var el = table.createForm();

            $(el).addClass("col-md-4");

            $(o.data).empty().append(el);
        }
    });
}


function doReport(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("View report here.");
    var section = bigAndSmall();
    var firstBox = section.firstBox();

    firstBox.setHeader("....");
    firstBox.isLarge();
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            l.hide();
            var json = ctrl.jsonParse(res);

            var pad = document.createElement("div");
            pad.className = "ten-pad";
            var io = globalWidget();
            var newPanel = document.createElement("div");
            newPanel.className = "col-md-8 height-large text-left";
            var ul = List(json,
                {
                    onCreate: function (a, array, el) {
                        var i = radioWrapper(el.value);
                        i.setName("data-change");
                        i.input.onchange = function () {
                            var loaderL = ctrl.loader(pad);
                            loaderL.show();
                            ctrl.request({
                                url: el.href,
                                success: function (res) {
                                    loaderL.hide();
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
                                        var lst = addCols(c, jsCols,
                                            {
                                                onMenu: function (a, li) {
                                                    li.remove();
                                                },
                                                onTree: function (o, li, ul, el) {
                                                    if (!el.access) return;
                                                    o.link.innerHTML = "";

                                                    var i2 = input("hidden", el.key, el.store);
                                                    var i3 = input("hidden", el.key, el.value);
                                                    var i4 = input("hidden", el.key, el.extra);

                                                    var isExtra = el.extra !== undefined && el.extra;


                                                    var checkB = checkWrapper(el.value);
                                                    checkB.input.onclick = function (ex) {
                                                        ex.stopPropagation();
                                                    };
                                                    checkB.input.onchange = function () {
                                                        chosenArray.remove(i4);
                                                        chosenArray.remove(i3);
                                                        chosenArray.remove(i2);
                                                        if (this.checked) {
                                                            chosenArray.push(i3);
                                                            chosenArray.push(i2);
                                                            if (isExtra) {
                                                                chosenArray.push(i4);
                                                            }
                                                        }
                                                    };
                                                    o.link.appendChild(checkB.parent);
                                                }
                                            }, undefined, ctrl);


                                        $(lst).addClass("margin-top-bottom");

                                        var div = document.createElement("div");

                                        div.appendChild(h1);
                                        div.appendChild(lst);

                                        return div;
                                    }

                                    var list2 = createList(js.columns, "Choose on list");

                                    var ripple = rippleButton("View Final Step");
                                    var loader = ctrl.loader(secondB.body);

                                    ripple.onclick = function () {
                                        var form = document.createElement("form");
                                        form.method = "POST";
                                        form.action = js.route;
                                        iterateJson(chosenArray, function (el) {
                                            form.appendChild(el);
                                        });
                                        loader.show();

                                        ctrl.tokenF(form, js.tokenName, js.tokenValue);

                                        ctrl.saveForm({
                                            form: form,
                                            success: function (res) {
                                                loader.hide();
                                                io.hideModal();

                                                var cAgain = rippleButton("<i class='fa fa-refresh'></i> Choose again");
                                                cAgain.onclick = function () {
                                                    io.showModal();
                                                };

                                                var c = ctrl.jsonParse(res);

                                                var form = {};

                                                c.formHead = {};
                                                form.form = c.content;
                                                var table = new DesignTable(form, firstBox.body, true);
                                                table.callBack = function (form) {
                                                    handleInReport(form, section.secondBox(), ctrl);
                                                };
                                                var ex = table.createForm(true);
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

                                }
                            });
                        };
                        a.appendChild(i.parent);
                    }
                });
            $(ul).addClass("text-left");
            pad.appendChild(ul);
            io.add(pad);
            $(io.box).addClass("padding");
        }
    });

    o.data.appendChild(section.parent);
}

function quickReport(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("View report here.");
    var section = bigAndSmall();
    var firstBox = section.firstBox();

    firstBox.setHeader("....");
    firstBox.isLarge();
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            l.hide();
            var json = ctrl.jsonParse(res);
            var f = ctrl.CreateForm(json);
            var arr = [
                {
                    type: "text",
                    calendar: true,
                    name: "start",
                    label: "Choose start date"
                },
                {
                    type: "text",
                    calendar: true,
                    name: "end",
                    label: "Choose end date"
                }
            ];
            f.addObject(arr);
            var loader = ctrl.loader(f.parent);
            f.submit(function () {
                loader.show();
                ctrl.saveForm({
                    form: f.form,
                    success: function (res) {
                        var js = ctrl.jsonParse(res);
                        var tab = newTab();
                        var head = [];
                        var data = [];
                        var table = tableStack(
                            {
                                onCreate: function (body,tHead) {
                                    $(body.parentNode).addClass("table-14");
                                    stackTr(js.fields, {
                                        onCreate: function (td,tr,elem) {
                                            var th = document.createElement("th");
                                            th.innerHTML = elem.title;
                                            head.push(elem.title);
                                            tHead.appendChild(th);
                                        }
                                    });
                                    stackRow(js.data,
                                        {
                                            onCreate: function (tr,el) {

                                                var sData = [];
                                                var t = stackTr(js.fields, {
                                                    onCreate: function (td,tr,elem) {
                                                        var v = el[elem.key];
                                                        var value = ctrl.isUndefined(v) ? undefined : v.toString();
                                                        td.innerHTML = value;
                                                        sData.push(value);
                                                    }
                                                });
                                                data.push(sData);
                                                body.appendChild(t);
                                            }
                                        });
                                }
                    });

                        var excel = new Excel();
                        excel.headerArray = head;
                        excel.dataArray = data;
                        excel.exportToCsv();

                        var content = document.createElement("div");
                        content.className = "invoice";
                        content.appendChild(reportHeader("f",js.title));
                        content.appendChild(table);

                        tab.document.body.appendChild(content);


                loader.hide();
            }
        })
}

)
;

firstBox.body.appendChild(f.parent);
}
})
;

o.data.appendChild(section.parent);
}

function oldSpareApprove(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("Approve old spare parts requests.");
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            l.hide();
            var json = ctrl.jsonParse(res);

            var ul = List(json.data,
                {
                    onCreate: function (a, li, el) {
                        var wr = checkWrapper(el.print);
                        a.appendChild(wr.parent);

                        var inp = document.createElement("input");
                        inp.type = "hidden";
                        inp.value = el.id;
                        inp.name = "old";

                        var div = document.createElement("form");
                        div.appendChild(inp);
                        div.method = "POST";
                        div.action = json.save;
                        ctrl.tokenF(div, json.tokenName, json.tokenValue);

                        var d = document.createElement("div");
                        d.innerHTML = "<p class=\"text-muted text-red well well-sm no-shadow\" style=\"margin-top: 10px;\">\n" +
                            "                                                        <span>Old parts with non existing serial number will be escaped </span>\n" +
                            "                                                        <i class=\"fa fa-info-circle big-font pull-right\"></i>\n" +
                            "                                                    </p>";
                        div.appendChild(d);

                        var arr = [];

                        var txt = textarea();
                        txt.success();
                        txt.setName("comment");
                        txt.setHolderAndLabel("Add comment");
                        txt.setHelp("required field");
                        div.appendChild(txt.parent);

                        arr.push({field: txt.input});

                        for (var i = 1; i <= el['quantity']; i++) {
                            var t = textarea(true);
                            t.setName("item");
                            t.setHolderAndLabel("Add old part serial number " + i);
                            div.appendChild(t.parent);
                            var ob = {};
                            ob.field = t.input;
                            arr.push(ob);
                        }

                        if( json.enabled ){
                            var wp = checkWrapper("Non registered spare parts");
                            wp.setName("escape");
                            div.appendChild(wp.parent);
                        }

                        wr.input.onchange = function () {
                            if (this.checked) {
                                confirmBox({
                                    title: "Add serial numbers",
                                    body: div,
                                    accept: function (e, wig, div, bx, dox) {
                                        var l = ctrl.loader(dox);
                                        var f = this.body;

                                        ctrl.validate(arr, function () {
                                            l.show();
                                            ctrl.saveForm({
                                                form: f,
                                                success: function (res) {
                                                    if (res !== "1") {
                                                        confirmBox({
                                                            title: "Response message",
                                                            message: res,
                                                            accept: function (e, wig) {
                                                                hide(wig);
                                                                a.parentNode.remove();
                                                                o.click();
                                                            },
                                                            cancel: function (e, wig) {
                                                                this.accept(e, wig);
                                                            }
                                                        })
                                                    } else {
                                                        hide(wig);
                                                        a.parentNode.remove();
                                                        o.click();
                                                    }
                                                    this.form.reset();
                                                    l.hide();
                                                }
                                            });
                                        });

                                    }
                                });
                            }
                        };


                        wr.setName(el.unique);
                        wr.setValue(el.id);
                    }
                });

            var col = ctrl.centerCol();

            col.removeFooter();

            var form = document.createElement("div");

            form.appendChild(ul);
            col.body.appendChild(form);

            o.data.appendChild(col.parent);
        }
    });
}

function oldSpareGApprove(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("Approve old spare parts requests.");
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            l.hide();
            var json = ctrl.jsonParse(res);

            var foreManComment = '', gComment = '';
            var map = [];
            var ul = List(json.data,
                {
                    onCreate: function (a, li, el) {
                        var arr = [];
                        var wr = checkWrapper(el.print);
                        a.appendChild(wr.parent);
                        wr.setValue(el.id);
                        if (ctrl.isObject(json.stock)) {
                            wr.setName(el.unique);
                            var din = document.createElement("div");
                            var select = stackSelectWith(json.cars, undefined, el.unique, undefined, "Choose car", undefined, true);
                            din.appendChild(select.parent);
                            var select2 = stackSelectWith(json.spares, undefined, el.unique, undefined, "Choose new part", undefined, true);
                            din.appendChild(select2.parent);
                            var nm = "m"+el.unique;
                            var select3 = stackSelectWith(json.mechanic, undefined, nm, undefined, "Choose mechanic", undefined, true);
                            //din.appendChild(select3.parent);
                            var s4 = [
                                {
                                    id:1,
                                    print:"Internal mechanic"
                                },
                                {
                                    id:2,
                                    print:"External mechanic"
                                }
                            ];
                            var select4 = stackSelectWith(s4, undefined, el.unique, undefined, "Choose mechanic", undefined, true);
                            din.appendChild(select4.parent);

                            var text = textarea(true);
                            text.setName(nm);
                            text.setHolderAndLabel("Enter name of the mechanic");
                            //din.appendChild(text.parent);

                            arr.push({field: select.input});
                            arr.push({field: select2.input});
                            arr.push({field: select4.input});
                            var fx = {field: select3.input};
                           // arr.push(fx);
                            var f = {field: text.input};
                            //arr.push(f);

                            select4.input.onchange = function () {
                                if( this.value === "1" ){
                                    arr.remove(f);
                                    text.parent.remove();
                                    arr.push(fx);
                                    din.appendChild(select3.parent);
                                }else{
                                    arr.remove(fx);
                                    select3.parent.remove();
                                    arr.push(f);
                                    din.appendChild(text.parent);
                                }
                            };

                            wr.input.onchange = function () {
                                var i = this;
                                if (!this.checked) return false;

                                map.remove(select.input);
                                map.remove(select2.input);
                                map.remove(select3.input);
                                map.remove(text.input);
                                confirmBox({
                                    title: "Choose car & new spare",
                                    body: din,
                                    array: arr,
                                    accept: function (e, wig) {
                                        ctrl.validate(this.array, function () {
                                            map.push(select.input);
                                            map.push(select2.input);
                                            if(select4.input === "1")
                                            map.push(select3.input);
                                            else map.push(text.input);
                                            hide(wig);
                                        });
                                    },
                                    cancel: function () {
                                        i.checked = false;
                                    }
                                });
                            };
                        } else {
                            wr.setName("item");
                        }


                        foreManComment = el['fComment'];
                        gComment = el['gComment'];
                    }
                });

            var col = ctrl.centerCol();

            col.fixFooter();

            var form = document.createElement("form");


            form.appendChild(ul);
            form.method = "POST";
            form.action = json.save;
            ctrl.tokenF(form, json.tokenName, json.tokenValue);

            var bu = button("Approve selected");

            var d = document.createElement("div");

            if (ctrl.isObject(json.garage)) {
                d.appendChild(ctrl.displayB(foreManComment, "Fore man comment"));
            }
            if (ctrl.isObject(json.stock)) {
                d.appendChild(ctrl.displayB(foreManComment, "Fore man comment"));
                d.appendChild(ctrl.displayB(gComment, "Garage manager comment"));
            }

            var tArea = textarea();
            tArea.setName("comment");
            tArea.setHolderAndLabel("Add comment on this approval");
            d.appendChild(tArea.parent);

            bu.block();
            bu.parent.onclick = function () {
                confirmBox({
                    title: "Approve",
                    body: d,
                    accept: function (e, wig) {
                        hide(wig);
                        var ld = ctrl.loader(col.box);
                        ld.show();
                        hide(this.body);
                        map.forEach(function (value) {
                            $(value).addClass("hidden");
                            form.appendChild(value);
                        });

                        form.appendChild(this.body);
                        ctrl.saveForm({
                            form: form,
                            success: function (res) {
                                ctrl.jsonParse(res);
                                ld.hide();
                                o.click();
                            }
                        });
                    }
                });


            };

            col.body.appendChild(form);
            col.footer.appendChild(bu.parent);

            o.data.appendChild(col.parent);
        }
    });
}


function addToStock(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("Add approved requests to stock.");
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            l.hide();
            var json = ctrl.jsonParse(res);


            var gManagerComment = '', tMdComment = '', procurementComment = '', logisticComment = '';
            var ul = List(json.data,
                {
                    onCreate: function (a, li, el) {
                        var wr = checkWrapper(el.print);
                        a.appendChild(wr.parent);
                        var input = document.createElement("input");
                        input.type = "hidden";
                        input.value = el.id;
                        input.name = "request";

                        var div = document.createElement("form");
                        div.appendChild(input);
                        div.method = "POST";
                        div.action = json.save;
                        ctrl.tokenF(div, json.tokenName, json.tokenValue);


                        gManagerComment = el['garageMComment'];
                        tMdComment = el['directorComment'];
                        procurementComment = el['procurementComment'];
                        logisticComment = el['logisticComment'];

                        var place = document.createElement("div");

                        if (ctrl.isObject(json.stock)) {
                            place.appendChild(ctrl.displayB(gManagerComment, "Garage manager"));
                            place.appendChild(ctrl.displayB(tMdComment, "Transport managing director"));
                            place.appendChild(ctrl.displayB(procurementComment, "Procurement manager"));
                        }


                        place.appendChild(div);

                        var supplier = textarea(true);
                        supplier.success();
                        supplier.setName("supplier");
                        supplier.setHelp("Supplier name are required");
                        supplier.setHolderAndLabel("Add supplier names");

                        var arr = [];
                        arr.push({field: supplier.input});
                        div.appendChild(supplier.parent);
                        for (var i = 1; i <= el['quantity']; i++) {
                            var o = {};
                            var t = textarea(true);
                            t.setName("item");
                            t.setHolderAndLabel("Add item serial number " + i);
                            div.appendChild(t.parent);
                            o.field = t.input;
                            arr.push(o);
                        }

                        wr.input.onchange = function () {
                            var check = this;
                            if (check.checked) {
                                confirmBox({
                                    title: el.print,
                                    body: place,
                                    form: div,
                                    accept: function (e, wig, div, box, x) {
                                        var l = ctrl.loader(x);
                                        var b = this.form;
                                        ctrl.validate(arr, function () {
                                            l.show();
                                            ctrl.saveForm({
                                                form: b,
                                                success: function () {
                                                    hide(wig);
                                                    l.hide();
                                                    a.parentNode.remove();
                                                }
                                            });
                                        });
                                    },
                                    cancel: function () {
                                        check.checked = false;
                                    }
                                })
                            }
                        };
                        wr.setName(el.unique);
                        wr.setValue(el.id);
                    }
                });

            var col = ctrl.centerCol();
            col.removeFooter();

            var form = document.createElement("form");

            form.appendChild(ul);
            form.method = "POST";
            form.action = json.save;
            ctrl.tokenF(form, json.tokenName, json.tokenValue);


            col.body.appendChild(form);

            o.data.appendChild(col.parent);
        }
    });
}

function changeRole(ctrl, o) {
    var l = ctrl.loader(o.tab);
    l.show();
    o.addHeader("Change user role.");
    ctrl.request({
        url: o.link.href,
        success: function (res) {
            l.hide();
            var json = ctrl.jsonParse(res);

            var ix = null;

            var ul = List(json.data,
                {
                    onCreate: function (a, li, el) {
                        var wr = radioWrapper(el.print);
                        wr.setValue(el.path);
                        wr.setName("name");
                        wr.input.onchange = function () {
                            ix = this.value;
                        };
                        a.appendChild(wr.parent);
                    }
                });

            var col = ctrl.centerCol();

            col.fixFooter();

            var bu = button("Change role");

            bu.block();
            bu.parent.onclick = function () {
                if (ix === null) return false;
                var ld = ctrl.loader(col.box);
                ld.show();
                ctrl.request({
                    url: ix,
                    success: function (res) {
                        ctrl.resetPage(res);
                    }
                });
            };

            col.body.appendChild(ul);
            col.footer.appendChild(bu.parent);

            o.data.appendChild(col.parent);
        }
    });
}


function signOut(ctrl, o) {
    o.addHeader("Logout options");
    confirmBox({
        title: "Sign out",
        content: "Are you sure to logout ?",
        accept: function (a, wig, div, d) {
            var ld = ctrl.loader(d.parentNode);
            ld.show();
            ctrl.request({
                url: o.link.href,
                success: function (res, xhr) {
                    ctrl.loginOr(xhr);
                    hide(wig);
                    ld.hide();
                }
            });
        }
    });
}


function newTab() {
    var newWindow = window.open();
    newWindow.document.head.innerHTML = document.head.innerHTML;
    return newWindow;
}

function deepValues(row, el, td) {
    if (el.father === undefined) return;
    var f = el.father.split(".");
    var elem = row;
    if (elem === null || elem === undefined) return;
    for (var i = 0; i < f.length; i++) {
        elem = elem[f[i]];
        if (elem === undefined || elem === null) return;
    }

    var value = elem[el.key];

    return expressValue(value, td);
}


function fileChooser() {
    var div = document.createElement("div");
    div.className = "form-group relative";
    var input = document.createElement("input");
    input.type = "file";

    var label = document.createElement("label");
    label.innerHTML = "Choose file";

    var button = document.createElement("button");
    button.className = "btn btn-block bt-sm btn-default";
    button.innerHTML = "<i class='fa fa-file-photo-o'></i> <span>Choose file</span>";

    div.appendChild(input);
    hide(input);
    div.appendChild(label);
    div.appendChild(button);

    var s = document.createElement("span");
    s.innerHTML = "";
    div.appendChild(s);

    button.onclick = function (ev) {
        input.click();
    };

    input.onchange = function (ev) {

        $(s).empty();

        if(!this.value) return;

        var name = fileName(this.value);
        s.innerHTML = "<p class=\"text-muted text-red well well-sm no-shadow\" style=\"margin-top: 10px;\">\n" +
            "                                                        <span>"+name+"</span>\n" +
            "                                                        <i class=\"fa fa-info-circle big-font pull-right\"></i>\n" +
            "                                                    </p>";

    };

    return {
        parent:div,
        button:button,
        input:input,
        label:label,
        setName:function ( name ) {
            this.input.name = name;
        },
        escape:function(){
            this.input.setAttribute("data-escape","1");
        },
        setLabel:function ( label ) {
            this.label.innerHTML = label;
        }
    }
}




