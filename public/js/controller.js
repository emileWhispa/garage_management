function Controller() {

    this.headers = {};

    this.mainRequest = {};

    this.empty = function () {
        $(document.body).empty();
    };

    this.defIcon = "";

    this.loginOr = function ( req ) {

        var r = req;


        this.headers = r.getAllResponseHeaders();
        this.mainRequest = r;
        this.defIcon = r.getResponseHeader("defaultIcon");

        var o = {
            route:r.getResponseHeader("loginUrl"),
            title:"Login form",
            tokenName:r.getResponseHeader("tokenName"),
            tokenValue:r.getResponseHeader("tokenValue")
        };


        var xHeader = r.getResponseHeader("system-access");
        if( !this.isUndefined(xHeader) ){
            var data = r.responseText;
            this.resetPage(data);
        }else
        {
            this.empty();
            var form = this.CreateForm(o);

            form.column.style.marginTop = "70px";

            $(form.column).removeClass("col-md-4").addClass("login-box");

            var widget = this.widget();
            widget.put(form.form);


            var main = this;
            form.submit(function (f) {
                var l = main.loader(f);
                l.show();
                main.saveForm({
                    form: f,
                    success: function (res, xhr) {
                        var status = xhr.getResponseHeader("status");

                        if (status !== "1") {
                            form.updateError("Login failed, check username or password");
                        } else {
                            widget.extend();
                            var json = main.jsonParse(res);

                            var ix = null;

                            var list = List(json,
                                {
                                    onCreate: function (a, li, el) {
                                        var name = el['role']['roleName'];
                                        var wr = radioWrapper(name);
                                        wr.setValue(el["path"]);
                                        wr.setName("name");
                                        wr.input.onchange =function () {
                                            ix = this.value;
                                        };
                                        a.appendChild(wr.parent);
                                    }
                                });

                            var div = document.createElement("div");
                            div.appendChild(list);

                            var bu = button("SIGN IN");
                            bu.right();
                            bu.parent.onclick = function () {
                                 main.request({
                                    type: "GET",
                                    url: ix,
                                    success: function (res, xhr) {
                                        var n = xhr.getResponseHeader("system-access");

                                        if (n !== null) {
                                            main.resetPage(res);
                                        }

                                    }
                                });
                            };
                            div.appendChild(bu.parent);

                            widget.data(div);
                        }

                        l.hide();
                    }
                })
            });

            form.addToBody();

            form.addLogo(r.getResponseHeader("loginImg"));

            form.setCol();

            form.centered();

            var fields = [
                {
                    label: "Enter username",
                    name: "username"
                },
                {
                    label: "password",
                    type: "password",
                    name: "password"
                },
                {
                    label: "Remember me ?",
                    type: "checkbox",
                    value:"1"
                }
            ];

            form.addObject(fields);

        }
    };

    this.isValue = function (func, type) {
        return func !== undefined && func !== null && typeof func === type;
    };

    this.isNumber = function (func) {
        return this.isValue(func, "number");
    };

    this.isFunc = function (func) {
        return this.isValue(func, "function");
    };

    this.isObject = function (func) {
        return this.isValue(func, "object");
    };

    this.isUndefined = function (func) {
        return func === undefined || func === null;
    };

    this.columns = function (data) {
            var obj = data;
            var cols = [];
            var o = obj[0] !== undefined ? obj[0] : obj;
            for (var key in o ) {
                cols.push(key);
            }
            return cols;
    };

    var self = this;

    this.tokenF = function (form , name , value ) {
        var input = document.createElement("input");
        input.type = "hidden";
        input.name = name;
        input.value = value;
        form.appendChild(input);
    };

    this.saveForm = function (obj) {
        var form = obj.form;

        if (form === undefined) return;

        var url = form.action;
        var formData = new FormData(form);
        var xhr = new XMLHttpRequest();

        var t = obj.type !== undefined ? obj.type.toUpperCase() : "POST";


        xhr.open(t, url, true);


        xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        xhr.setRequestHeader('form-data', 'XMLHttpRequest');
        xhr.onload = function () {
            if (xhr.status >= 200 && xhr.status < 400) {
                if (self.isFunc(obj.success)) obj.success(xhr.responseText, xhr);
            } else {
                if (self.isFunc(obj.error)) obj.error(xhr.responseText, xhr);
            }
        };

        xhr.onerror = function (error) {
            if (self.isFunc(obj.error)) obj.error(error, xhr);
        };
        xhr.send(formData); //Send to server

    };
    this.request = function (obj) {

        if (obj.url === undefined) return;

        var url = obj.url;

        var xhr = new XMLHttpRequest();

        var t = obj.method !== undefined ? obj.method.toUpperCase() : "GET";


        xhr.open(t, url, true);


        xhr.setRequestHeader('X-Requested-With', 'XMLHttpRequest');
        xhr.onload = function () {
            if (xhr.status >= 200 && xhr.status < 400) {
                if (self.isFunc(obj.success)) obj.success(xhr.responseText, xhr);
            } else {
                if (self.isFunc(obj.error)) obj.error(xhr.responseText, xhr);
            }
        };

        xhr.onerror = function (error) {
            if (self.isFunc(obj.error)) obj.error(error, xhr);
        };
        xhr.send(); //Send to server

    };

    this.checkLen = function ( f , min ) {
        var value = f.value.length >= min;
        if( value ){
            return true;
        }else{
            f.focus();
            return false;
        }
    };

    this.displayB = function ( text , title ) {
        var div = document.createElement("div");
        div.innerHTML = "<div class=\"box-body chat\" id=\"chat-box\" style=\"overflow: hidden; width: auto;\"><div class=\"item\">\n" +
            "                <img src=\""+this.defIcon+"\" alt=\"user image\" class=\"offline\" height='40'>\n" +
            "\n" +
            "                <p class=\"message\">\n" +
            "                  <a href=\"#\" class=\"name\">\n" +
            "                    <small class=\"text-muted pull-right\"><i class=\"fa fa-clock-o\"></i></small>\n" + title +
            "                  </a>\n" + text +
            "                </p>\n" +
            "              </div>";
        return div;
    };

    this.validate = function(obj, func ) {
        var valid = true;

        obj.forEach(function (value) {
            valid = valid && self.checkLen(value.field,1) || value.escape;
        });


        if (valid) {
            if ( this.isFunc(func) ) func(valid, obj);
        }

        return valid;
    };

    this.CreateForm = function (obj) {
        var solid = document.createElement("div");
        solid.className = "box box-primary";
        var header = document.createElement("div");
        header.className = "box-header with-border";
        header.innerHTML = "<h3 class=\"box-title\"><i class=\"fa fa-edit\"></i> " + obj.title + " </h3>";
        solid.appendChild(header);

        var form = document.createElement("form");
        form.action = obj.route;
        form.method = "POST";
        this.tokenF(form,obj.tokenName,obj.tokenValue);
        var body = document.createElement("div");

        form.appendChild(body);
        body.className = "box-body";

        var logo = document.createElement("div");
        logo.className = "f-logo";
        body.appendChild(logo);

        var error = document.createElement("div");
        error.className = "text-red five-pad";
        $(error).hide();

        solid.appendChild(error);
        solid.appendChild(form);

        var footer = document.createElement("div");
        footer.className = "box-footer clearfix";

        var button = document.createElement("button");
        button.innerHTML = "Save changes";
        button.type = "submit";
        button.className = "btn btn-default pull-right";
        footer.appendChild(button);

        form.appendChild(footer);

        var div = document.createElement("div");
        div.className = "col-md-4";

        var fin = this;

        return {
            header: header,
            form: form,
            footer: footer,
            body: body,
            parent: solid,
            column: div,
            button: button,
            fieldsArray: [],
            logo: logo,
            error: error,
            updateError: function (error, t) {
                var e = $(this.error);
                e.show(300).html(error);
                setTimeout(function () {
                    e.hide(300);
                }, t === undefined ? 5200 : t);
            },
            submit:function (funcEvent) {
                var arr = this.fieldsArray;
                this.form.onsubmit = function (e) {
                    e.preventDefault();
                    var a = self.isFunc(funcEvent);
                    if (a) {
                        var form = this;
                        self.validate(arr,function () {
                            funcEvent(form);
                        });
                    }
                    return false;
                };
            },
            addContent: function (cont) {
                this.body.appendChild(cont);
            },
            addLogo: function (src) {
                var img = document.createElement("img");
                img.src = src;
                $(this.logo).addClass("form-logo");
                this.logo.appendChild(img);
            },
            addElem: function (elem) {
                elem.appendChild(this.parent);
            },
            addToBody: function () {
                this.addElem(document.body);
            },
            setCol: function () {
                document.body.appendChild(this.column);
                this.addElem(this.column);
            },
            centered: function () {
                $(this.column).addClass("col-centered");
            },
            addObject: function (obj) {
                var s = this;
                obj.forEach(function (v) {
                    var group = document.createElement("div");
                    group.className = "form-group";
                    var label = document.createElement("label");
                    var txt = "textarea";
                    var ix = v.type === txt ? txt : "input";
                    var input = document.createElement(ix);
                    input.autocomplete = "off";
                    input.className = v.type !== "checkbox" && v.type !== "radio" ? "form-control" : "";

                    input.type = v.type !== undefined ? v.type : "text";

                    input.placeholder = v.label;
                    input.name = v.name;
                    var dValue = v.defaultValue;
                    var value = v.value;

                    input.value = fin.isUndefined(value) ? "" : value;

                    input.disabled = v.disabled !== undefined && v.disabled;

                    var selectCondition = fin.isObject(value);
                    var isCheck = v.checked !== undefined && v.checked && !dValue;

                    var canPush = true;

                    if( selectCondition ){
                        var ox = isCheck ? stackCheckBox(value,undefined,v.name,dValue) : stackSelect(value,undefined,v.name,dValue,v.label);

                        label.textContent = v.label;
                        group.appendChild(label);
                        group.appendChild(ox);
                    }else if (v.type === "checkbox") {
                        canPush = false;
                        var wr = checkWrapper(v.label);
                        wr.setValue("true");
                        wr.setName(v.name);
                        wr.input.checked = value === "true";
                        group.appendChild(wr.parent);
                    }else if( v.upload ){
                        var f = fileChooser();
                        f.setName(v.name);
                        f.setLabel(v.label);
                        group = f.parent;
                        input = f.input;
                    } else {
                        label.textContent = v.label;
                        group.appendChild(label);
                        group.appendChild(input);

                        if( v.calendar ){
                            $(input).datepicker({format:"yyyy-mm-dd"});
                        } else if( v['timePicker'] ){
                            $(input).clockpicker();
                        }
                    }

                    s.body.appendChild(group);

                    var o = {
                        object: v,
                        label: label,
                        field: input
                    };

                    if(canPush) s.fieldsArray.push(o);

                });
            }
        };
    };

    this.jsonParse = function (res) {
        try {
            return JSON.parse(res);
        } catch (e) {
            console.log(e);
            return [];
        }
    };

    this.centerCol = function () {
        var div = document.createElement("div");
        div.className = "col-md-5 col-centered";

        var box = document.createElement('div');
        box.className = "box box-success height-large";

        var body = document.createElement("div");
        var footer = document.createElement("div");
        footer.className = "box-footer clear-fix";

        div.appendChild(box);
        box.appendChild(body);
        box.appendChild(footer);

        return {
            parent:div,
            box:box,
            body:body,
            footer:footer,
            warn:function () {
                $(this.box).removeClass('box-success').addClass("box-warning");
            },
            removeFooter:function () {
                this.footer.remove();
            },
            fixFooter:function () {
                $(this.footer)
                    .addClass("absolute")
                    .addClass("full-width")
                    .addClass("bottom-pos");
            }
        }
    };

    this.loader = function (form) {

        var cCheck = form.querySelector(".loading-pro");
        if (cCheck) cCheck.remove();

            var loading = document.createElement("div");
            $(loading).addClass("formLoader").addClass("loading-pro").addClass("didHide");
            var topBar = document.createElement("div");
            topBar.className = "load-bar absolute top-left";
            topBar.innerHTML = "<div class=\"bar\"></div><div class=\"bar\"></div><div class=\"bar\"></div><div class=\"bar\"></div>";
            loading.appendChild(topBar);
            var loader2 = document.createElement("div");
            loader2.className = "loader-new didHide";
            loading.appendChild(loader2);
            setInterval(function () {
                $(topBar).slideToggle();
                $(loader2).slideToggle();
            }, 9000);
            var innerLoad = document.createElement("div");
            $(innerLoad).addClass("formLoading");
            loading.appendChild(innerLoad);

            form.insertBefore(loading, form.firstChild);



        return {
            parent:loading,
            show:function () {
                $(this.parent).show();
            },
            hide:function () {
                $(this.parent).hide();
            }
        };
    };

    this.widget = function () {
        var doc = document.createElement("div");
        doc.className = "top-left absolute widget transit full-width";
        var display = document.createElement("div");
        display.className = "relative full-height display transit five-pad";

        var options = ["extended", "mini", "small", "zero-width"];

        $(display).addClass(options[3]);

        doc.onclick = function (ev) {
            if (ev.target === this) {
                options.forEach(function (value) {
                    $(display).removeClass(value);
                });

                $(display).addClass(options[3]);

                setTimeout(function () {
                    $(doc).fadeOut();
                }, 500);
            }
        };

        $([display, doc]).hide();

        doc.appendChild(display);

        return {
            parent: doc,
            display: display,
            current: options[1],
            options: options,
            showSlide: function () {
                $(this.parent).show();
                var d = $(this.display);
                var cur = this.current;
                this.options.forEach(function (value) {
                    d.removeClass(value);
                });

                d.show().addClass(this.options[3]);
                setTimeout(function () {
                    d.addClass(cur);
                }, 100)
            },
            right:function () {
                $(this.display).addClass("right");
            },
            put: function (elem) {
                elem.appendChild(this.parent);
                $(this.parent).animate({
                    height: "100%"
                }, "slow");
            },
            putAndShow: function (elem) {
                this.put(elem);
                this.showSlide();
            },
            extend: function () {
                this.current = this.options[0];
                this.showSlide();
            },
            minify: function () {
                this.current = this.options[1];
                this.showSlide();
            },
            small: function () {
                this.current = this.options[2];
                this.showSlide();
            },
            data: function (d) {
                $(this.display).empty();
                this.display.appendChild(d);
            }
        }
    };

    this.resetPage = function (res) {
        var json = this.jsonParse(res);
        this.empty();
        $(document.body).removeClass("loading").addClass("skin-blue-light fixed sidebar-mini");
        var ctrl = new MenuController();
        ctrl.homePage(json, this);
    }
}


function List(json, callback) {
    var ul = document.createElement("ul");
    ul.className = "nav nav-stacked bottom-space";
    var array = [];
    var click = callback !== undefined && callback.onClick !== undefined && typeof callback.onClick === "function";
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var li = document.createElement("li");
        var a = document.createElement("a");
        a.href = "#";
        a.onclick = function (ev) {
            $(array).removeClass("active");
            $(this.parentNode).addClass("active");
            if (click) callback.onClick(this, array, el, ev);
        };


        li.appendChild(a);
        ul.appendChild(li);
        array.push(li);

        if (create) callback.onCreate(a, array, el, i);
    });
    return ul;
}

function input(type, name, value) {
    var i = document.createElement("input");
    i.type = type;
    i.name = name;
    i.value = value;

    return i;
}

function button(value) {
    var i = document.createElement("button");
    i.type = "button";
    i.className = "btn btn-default";
    var icon = document.createElement("i");

    i.appendChild(icon);

    var sp = document.createElement("span");
    sp.innerText = value === undefined ? "" : value;

    i.appendChild(sp);

    return {
        parent: i,
        iconIcon:icon,
        span:sp,
        array:["btn-default","btn-success","btn-info","btn-warning","btn-danger"],
        block: function () {
            $(this.parent).addClass("btn-block");
        },
        icon:function ( ico ) {
            this.iconIcon.className = ico;
        },
        small:function () {
            $(this.parent).addClass("btn-sm");
        },
        danger:function () {
            var p = this.parent;
            this.array.forEach(function (value1) {
                $(p).removeClass(value1);
            });
            $(p).addClass(this.array[4]);
        },
        right: function () {
            $(this.parent).addClass("pull-right");
        }
    };
}


function radioWrapper(title) {
    var element = document.createElement("span");
    element.className = "t-radio";
    var input = document.createElement("input");
    input.type = "radio";
    element.appendChild(input);
    var uId = Math.random() * 100000000000000000;
    input.id = uId.toString();
    var span = document.createElement("label");
    span.textContent = title;
    span.setAttribute("for", uId.toString());
    element.appendChild(span);
    return {
        parent: element,
        label: span,
        input:input,
        setName:function (name) {
            this.input.name = name;
        },
        setValue:function (val) {
            this.input.value = val;
        }
    };
}


function checkWrapper(label) {
    var lab = document.createElement("label");
    lab.className = "fancy-checkbox";
    var input = document.createElement("input");
    input.type = "checkbox";
    input.value = label;
    lab.appendChild(input);
    var sp = document.createElement("span");
    sp.textContent = label;

    lab.appendChild(sp);

    return {
        parent:lab,
        input:input,
        setValue:function(val){
            this.input.value = val;
        },
        setName:function ( name ) {
            this.input.name = name;
        }
    };
}


function Tabs(json) {
    this.tab = document.createElement("div");
    this.ul = document.createElement("ul");
    this.tContent = document.createElement("div");
    this.liArray = [];
    this.ctrl = new Controller();

    this.init = function () {
        var ul = this.ul;
        var content = this.tContent;
        this.tab.className = "nav-tabs-custom";
        ul.className = "nav nav-tabs";
        this.tContent.className = "tab-content";

        this.tab.appendChild(this.ul);
        this.tab.appendChild(this.tContent);

        var arr = this.liArray;

        var c = this.ctrl;

        var links = json.link;

        if( c.isUndefined(links) ) return;

        links.sort(function (a,b) {
            return a.order - b.order;
        });

        links.forEach(function (value, k) {
            var id = new Date().getTime() + "-" + parseInt((Math.random() * 100000000000) + "ft").toString();
            var li = document.createElement("li");
            var a = document.createElement("a");
            a.setAttribute("data-toggle", "tab");
            a.href = "#" + id;
            a.title = value.href;
            var s = document.createElement("span");
            s.innerText = value.title;
            s.className = "margin-left-5";
            var iN = document.createElement("i");
            iN.className = value.icon;
            a.appendChild(iN);
            a.appendChild(s);

            var correspondingTab = document.createElement("div");
            correspondingTab.id = id;
            correspondingTab.className = k ? "tab-pane" : "tab-pane active";
            li.className = k ? "" : "active";

            var body = document.createElement("div");
            body.className = "tab-data relative";

            var foot = document.createElement("div");
            foot.className = "tab-foot";

            correspondingTab.appendChild(body);
            correspondingTab.appendChild(foot);

            a.onclick = function () {
                arr.forEach(function (v) {
                    $(v.tab).removeClass("active");
                });
                $(correspondingTab).addClass("active");

                c.request({
                    url:this.title,
                    success:function (res) {
                        var json = c.jsonParse(res);
                        var pg = new Pagination(json,{
                            onSuccess:function (res,r) {
                                var json = c.jsonParse(res);
                                var d = new DesignTable(json,body);
                                d.reRun = r;
                            },
                            onFail:function () {
                                console.log("error");
                            }
                        },{place:foot,page:body});
                        pg.callback();
                    }
                })

            };

            if( !k ) a.click();

            var o = {};
            o.liElement = li;
            o.link = a;
            o.id = id;
            o.icon = iN;
            o.object = value;
            o.tab = correspondingTab;
            o.tabBody = body;
            o.tabFoot = foot;

            arr.push(o);

            li.appendChild(a);

            ul.appendChild(li);
            content.appendChild(correspondingTab);
        });
    };

    this.get = function () {
        return this.tab;
    };

    this.init();
}


function Pagination(object, callback, place) {
    this.length = 0;
    this.reachedIndex = 0;
    this.array = [];
    this.unList = null;
    this.aPre = null;
    this.href = null;
    this.interval = 7;
    this.ctrl = new Controller();
    this.callback = function () {

    };

    this.create = function () {
        var v = callback !== undefined;
        var successDeclared = v && this.ctrl.isFunc(callback.onSuccess);
        var failDeclared = v && this.ctrl.isFunc(callback.onFail);
        var aPre = this.aPre;
        var array = this.array;
        place.page = place.page === undefined ? document.createElement("span") : place.page;

        var loader = this.ctrl.loader(place.page);
        loader.show();
        var func = this;
        this.ctrl.request({
            url: this.href,
            success: function (result,xhr) {
                loader.hide();
                if (successDeclared) callback.onSuccess(result,func, aPre, array, loader,xhr);
            },
            error: function () {
                loader.hide();
                if (failDeclared) callback.onFail(aPre, array, loader);
            }
        });
    };

    this.backElement = function () {
        var li = document.createElement("li");
        var aPre = document.createElement("a");
        aPre.href = "#";
        aPre.innerHTML = "«";
        var self = this;
        aPre.onclick = function () {

            if (!self.reachedIndex) return false;

            --self.reachedIndex;
            self.navigate(true);
            return false;
        };

        li.appendChild(aPre);
        return li;
    };

    this.forwardElement = function () {
        var li = document.createElement("li");
        var aPre = document.createElement("a");
        aPre.href = "#";
        aPre.innerHTML = "»";
        var self = this;
        aPre.onclick = function () {

            if (self.reachedIndex === self.length) return false;

            ++self.reachedIndex;
            self.navigate();
            return false;
        };
        li.appendChild(aPre);

        return li;
    };

    this.removeAll = function () {
        var array = this.array;
        if (array) {
            iterateJson(array, function (el) {
                $(el.li).removeClass("active");
            })
        }
    };

    this.navigate = function (isBack) {

        var obj = this.array[this.reachedIndex];

        if (obj === undefined) return false;

        this.href = obj.obj.value;

        this.removeAll();
        $(obj.li).addClass("active");

        this.create();

        if (this.reachedIndex >= this.interval) {

            var x = this.reachedIndex + 1;
            var j = this.array[x];
            var ij = this.array[x - this.interval];
            if (j) {
                if (isBack) $(j.li).hide();
                else $(j.li).show();

                if (ij && isBack) $(ij.li).show();
                else if (ij) $(ij.li).hide();
            }

        }
    };

    this.init = function () {
        this.length = object.length;
        var self = this;

        var ul = document.createElement("ul");
        self.unList = ul;

        $(ul).addClass("pagination pagination-sm no-margin pull-right");

        self.aPre = self.backElement();

        ul.appendChild(self.aPre);

        Array.prototype.forEach.call(object, function (el, i) {
            var li = document.createElement("li");
            var aHref = document.createElement("a");
            aHref.innerHTML = el.number;
            li.appendChild(aHref);
            aHref.onclick = function () {
                self.reachedIndex = el.number - 1;
                self.navigate();

                return false;
            };
            li.setAttribute("value", i);
            aHref.href = el.value;
            var o = {
                li: li,
                link: aHref,
                obj: el
            };

            self.array.push(o);

            if (i === 7) {
                var navElem = document.createElement("li");
                var aE = document.createElement("a");
                navElem.appendChild(aE);
                aE.innerHTML = "...";
                ul.appendChild(navElem);
                $(li).hide();
            } else if (i > 7) $(li).hide();

            ul.appendChild(li);
        });

        ul.appendChild(self.forwardElement());

        this.navigate();

        $(place.place).empty();

        place.place.appendChild(ul);
    };

    this.init();
}

function show(el) {
    if (el.length === undefined) {
        el.style.display = "block";
    } else {
        for (var i = 0; i < el.length; i++) {
            el[i].style.display = "block";
        }
    }
}

function hide(el) {
    if (el.length === undefined) {
        el.style.display = "none";
        if( getStyle(el,"position") === "fixed" || $(el).hasClass("light-widget") ){
            modalClose();
        }
    } else {
        for (var i = 0; i < el.length; i++) {
            el[i].style.display = "none";
        }
    }
}


function getStyle(elem, prop) {
    return document.defaultView.getComputedStyle(elem, null).getPropertyValue(prop);
}

function iterateJson(json, callBack) {
    Array.prototype.forEach.call(json, function (el, i) {
        if (callBack !== undefined && typeof callBack === "function") {
            callBack(el, i);
        }
    });
}



function DesignTable(json, place , noAuto ) {
    var self = this;
    this.place = place;
    this.newPlace = null;
    this.jsonForm = null;
    this.callBack = undefined;
    this.ctrl = new Controller();
    this.reRun = null;
    this.createForm = function ( noColon ) {
        if (json.form === undefined || typeof json.form !== "object") return;

        var jForm = this.jsonForm != null ? this.jsonForm : json.form;

        this.jsonForm = jForm;

        var head = jForm['formHead'];
        var data = jForm.formData;

        data.sort(function (a,b) {
            return a.order - b.order;
        });

        if ( !this.ctrl.isObject(data) || !this.ctrl.isObject(head) ){
            console.log("error");
            return;
        }

        var token = head.token;


        var o = {
            title:head["formName"],
            route:head["saveRoute"],
            tokenName:token.tokenName,
            tokenValue:token.tokenValue
        };

        var c = this.ctrl;
        var reRun = this.reRun;
        var cBack = this.callBack;

        var form = c.CreateForm(o);

        form.addObject(data);

        var ld = c.loader(form.parent);

        form.submit(function () {
            ld.show();
            c.saveForm({
                form:form.form,
                success:function (res,xhr) {
                    ld.hide();
                    var error = xhr.getResponseHeader("error");
                    if( error ){
                        form.updateError(error);
                        return;
                    }
                    if( c.isFunc(cBack) ) cBack(res);
                    if( c.isObject(reRun) ) reRun.navigate();
                }
            });
        });


        var formElement = form.parent;

        var colon = document.createElement("div");
        colon.className = "col-md-10 col-centered";
        colon.appendChild(formElement);

        return noColon ? formElement : colon;
    };

    this.slim = function () {
        var div = document.createElement("div");
        div.id = "issue";
        div.className = "text-center";
        div.style.marginTop = "5px";
        var div2 = document.createElement("div");
        div2.className = "text-center in-block";
        var span = document.createElement("span");
        span.textContent = "!";
        var label1 = document.createElement("label");
        label1.textContent = "No Content Found...";
        var p = document.createElement("p");
        p.textContent = "Click add new to add more contents of this page";
        div2.appendChild(span);
        div.appendChild(div2);
        div.appendChild(label1);
        div.appendChild(p);
        return div;
    };

    this.setDefaultJson = function () {
        this.jsonForm = json.form;
    };

    this.init = function () {
        this.place.innerHTML = "";
        var nForm = document.createElement("div");


        nForm.className = "new-form height-large no-shadow";
        var widget = document.createElement("div");
        widget.className = "form-widget didHide";
        nForm.appendChild(widget);
        var handle = document.createElement("div");
        handle.className = "table-handle";


        this.newPlace = handle;

        var deleteLink = document.createElement("a");
        deleteLink.className = "btn btn-sm margin-left bg-light-blue-gradient";
        deleteLink.innerHTML = "<i class=\"fa fa-refresh bg-light-blue-gradient\"></i>";

        var button = document.createElement("button");
        button.className = "btn btn-default btn-sm only-func";
        button.innerHTML = "<i class='fa fa-plus'></i> Create new";
        var c= this.ctrl;
        var node = c.widget();

        node.put(this.place);
        button.onclick = function () {
            self.setDefaultJson();
            var f = self.createForm();
            node.data(f);
            node.small();
        };

        if( json["newDisabled"] ){
            button.disabled = true;
        }


        handle.appendChild(button);
        //handle.appendChild(deleteLink);
        nForm.appendChild(handle);

        place.appendChild(nForm);

        this.createListTable();
    };


    this.createListTable = function () {
        var isTable = this.ctrl.isObject(json.page);

        if (!isTable) {
            console.log(json);
            return;
        }

        var table = json.page;



        var c = this.ctrl;

        var update = c.widget();

        update.right();

        update.put(this.place);


        var rRun = this;

        var rTable = stackTable(table, {
            onCreate: function (body, tHead, el, inc) {
                el.sort(function (a,b) {
                   return a.order - b.order;
                });
                var valueTr = stackTr(el, {
                    onCreate: function (td, tr, eli) {
                        var o = eli;
                        if ( !c.isObject(o) ) return;
                        var isHtml = o["html"] !== undefined;
                        var value = o["value"];
                        var row = document.createElement("div");

                        var c1 = document.createElement("div");
                        c1.className = "col-md-6";
                        var c2 = document.createElement("div");
                        c2.className = "col-md-6";

                        td.appendChild(row);
                        if( o.button ){
                            var b = button();
                            b.small();
                            b.icon("fa fa-edit");
                            b.parent.onclick = function () {
                                update.small();
                                var lo = c.loader(update.display);
                                lo.show();
                                c.request({
                                    url:value,
                                    success:function (res) {
                                        var json = c.jsonParse(res);
                                        var head = json["formHead"];
                                        var token = head.token;
                                        var o = {
                                            title:head["formName"],
                                            route:head["saveRoute"],
                                            tokenName:token.tokenName,
                                            tokenValue:token.tokenValue
                                        };
                                        var form = c.CreateForm(o);
                                        form.addObject(json.formData);

                                        var fLoader = c.loader(form.form);

                                        form.submit(function () {
                                            fLoader.show();
                                            c.saveForm({
                                                form:form.form,
                                                success:function (res,xhr) {
                                                    fLoader.hide();
                                                    var error = xhr.getResponseHeader("error");
                                                    if( error ){
                                                        form.updateError(error);
                                                        return;
                                                    }
                                                        if (c.isObject(rRun.reRun) ) rRun.reRun.navigate();

                                                }
                                            })
                                        });

                                        update.data(form.parent);
                                        lo.hide();
                                    }
                                })
                            };
                            if( json["newDisabled"] ){
                                b.parent.disabled = true;
                            }
                            td.appendChild(b.parent);
                        }else if( o.delete ){

                            var del = button();
                            del.small();
                            del.danger();
                            del.parent.onclick = function () {
                                this.value = value;
                                var dlt = document.createElement("div");
                                dlt.className = "table-responsive";
                                var span = document.createElement("div");
                                span.className ="big-font text-bold bottom-bottom";
                                span.innerHTML = "Delete this";
                                var table = document.createElement("table");
                                table.className = "table table-striped";
                                var bdy = document.createElement("tbody");

                                if( !tr ) return false;

                                var trNew = tr.cloneNode(true);

                                bdy.appendChild(trNew);
                                table.appendChild(bdy);
                                dlt.appendChild(span);
                                dlt.appendChild(table);

                                alertBody(this,dlt,"DELETE",c);
                            };

                            if( json["newDisabled"] ){
                                del.parent.disabled = true;
                            }

                            del.icon("fa fa-trash-o");
                            td.appendChild(del.parent);
                        }else if( o.file ){
                            o.badge = "";
                            var span = document.createElement("a");
                            span.className = "btn btn-link";
                            span.href = value;
                            span.target = "_blank";
                            span.textContent = "View attachment";
                            td.appendChild(span);
                        } else {
                            if( typeof value === "number" ) value = value.toLocaleString();
                            o.badge = isHtml ? td.innerHTML = value : td.textContent = value;
                        }


                        if (!inc) {
                            var th = document.createElement("th");
                            th.textContent = eli.label;
                            tHead.appendChild(th);
                        }

                    }
                });
                body.appendChild(valueTr);
            }
        });

        if (!table.length) {
            console.log(table.length);
            rTable = this.slim();
        }

        this.newPlace.appendChild(rTable);


    };

    this.padSlim = function () {
        var pad = document.createElement("div");
        pad.className = "ten-pad";
        pad.appendChild(this.slim());
        return pad;
    };

    if( noAuto === undefined ) this.init();
}


function stackTable(json, callback) {
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
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";

    if( json && typeof json === "object" ) {
        Array.prototype.forEach.call(json, function (el, i) {

            if (create) callback.onCreate(body, trH, el, i);
        });
    }
    return ul;
}

function stackTr(json, callback) {
    var ul = document.createElement("tr");

    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    var click = callback !== undefined && callback.onClick !== undefined && typeof callback.onClick === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var td = document.createElement("td");
        ul.appendChild(td);
        if (create) callback.onCreate(td, ul, el, i);
        if (click){
            td.onclick = function () {
                callback.onClick(td, ul, el, i);
            };
        }
    });
    return ul;
}

function stackRow(json, callback) {
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var td = document.createElement("tr");
        if (create) callback.onCreate(td , el, i);
    });
}




function stackCheckBox(json, callback, name, dValue) {
    var box = document.createElement("div");
    var searchValue = document.createElement("div");
    searchValue.className = "form-group";
    var iSearch = document.createElement("input");
    iSearch.placeholder = "Filter rows";
    iSearch.className = "form-control input-sm";
    searchValue.appendChild(iSearch);

    var defaultValue = document.createElement("option");
    defaultValue.value = "";
    defaultValue.innerHTML = "-- Select from list here --";

    var selectedData = document.createElement("div");
    selectedData.className = "form-group";

    var label = document.createElement("span");
    label.className = "text-center text-bold pointer";

    var sm = document.createElement("span");
    sm.innerText = "Selected list";
    var badge = document.createElement("span");
    badge.className = "badge bg-green margin-left-5";
    badge.innerText = "0";

    label.appendChild(sm);
    label.appendChild(badge);

    var selectedDiv = document.createElement("div");
    hide(selectedDiv);

    selectedData.appendChild(selectedDiv);

    label.onclick = function () {
        $(selectedDiv).slideToggle();
    };


    box.appendChild(label);
    box.appendChild(selectedData);

    box.appendChild(searchValue);

    var unSelected = document.createElement("div");
    box.appendChild(unSelected);

    var searchObject = {
        json: json,
        gang: 0,
        keyUp: function (js) {
            checkValues(js, unSelected, selectedDiv, callback, name, dValue, this);
        },
        removeKey: function (key) {
            ++this.gang;
            badge.innerText = this.gang.toString();
            this.json.remove(key);
        },
        addKey: function (key) {
            --this.gang;
            badge.innerText = this.gang.toString();
            this.json.push(key);
        }
    };

    iSearch.setAttribute("data-escape", "1");

    iSearch.onkeyup = function () {
        var js = searchJson(searchObject.json, "print", this.value);
        unSelected.innerHTML = "";
        $(selectedDiv).hide(300);
        searchObject.keyUp(js);
    };


    checkValues(json, unSelected, selectedDiv, callback, name, dValue, searchObject);

    return box;
}

function checkValues(json, unSelected, selectedDiv, callback, name, dValue, otherJson) {
    var array = [];
    var create = callback !== undefined && callback.onCreate !== undefined && typeof callback.onCreate === "function";
    Array.prototype.forEach.call(json, function (el, i) {
        var option = document.createElement("div");
        $(option).addClass("form-group");
        var id = el["id"] !== undefined ? el["id"] : 0;

        var wrapper = checkWrapper(defValue(el));

        wrapper.setValue(id.toString());

        itemMove(wrapper.input, option, unSelected, selectedDiv, el, otherJson);



        option.selected = id === dValue;

        option.appendChild(wrapper.parent);

        unSelected.appendChild(option);
        array.push(option);

        if (create) callback.onCreate(option, array, el, i);
    });
}

function itemMove(input, item, un, selected, el, otherJson) {
    input.onchange = function () {
        if (this.checked) {
            selected.appendChild(item);
            otherJson.removeKey(el);
        } else {
            un.appendChild(item);
            otherJson.addKey(el);
        }
    }
}


function textarea( t ) {
    var text = t === undefined ? "textarea" : "input";
    var div = document.createElement("div");
    div.className = "form-group relative";
    var label = document.createElement("label");
    label.innerHTML = "...";
    var input = document.createElement(text);
    input.className = "form-control";

    var help = document.createElement("span");
    help.className = "help-block hidden";

    var helpContext = document.createElement("span");
    helpContext.className = "margin-left-5";
    var icon = document.createElement("span");
    icon.innerHTML = "<i class='fa fa-exclamation-circle'></i>";

    help.appendChild(icon);
    help.appendChild(helpContext);

    div.appendChild(label);
    div.appendChild(input);
    div.appendChild(help);

    return {
        parent:div,
        input:input,
        label:label,
        help:help,
        icon:icon,
        hContext:helpContext,
        setName:function ( name ) {
            this.input.name = name;
        },
        setLabel:function ( label ) {
            this.label.innerHTML = label;
        },
        setHolder:function ( label ) {
            this.input.placeholder = label;
        },
        success:function () {
            $(this.parent).addClass("has-success");
        },
        setHelp:function ( text ) {
            $(this.help).removeClass("hidden");
            $(this.hContext).text(text);
        },
        setValue:function (val) {
            this.input.value = val;
        },
        isNumber:function () {
            this.input.type = "number";
        },
        disabled:function () {
            this.input.disabled = true;
        },
        hide:function () {
            $(this.parent).hide();
        },
        show:function () {
            $(this.parent).show();
        },
        readOnly:function () {
            this.input.readOnly = true;
        },
        notReadOnly:function () {
            this.input.readOnly = false;
        },
        setHolderAndLabel:function ( label ) {
            this.setHolder(label);
            this.setLabel(label);
        }
    }
}

function select() {
    var div = document.createElement("div");
    div.className = "form-group";
    var se = document.createElement("select");
    se.className = "form-control";

    var l = document.createElement("label");
    l.textContent = "Choose here";

    div.appendChild(l);
    div.appendChild(se);

    return {
        parent:div,
        label:l,
        select:se,
        setName:function ( name ) {
            this.select.name = name;
        },
        add:function ( array ) {
            var s = this.select;
            array.forEach(function (value) {
                var option = document.createElement("option");
                option.value = value.value;
                option.innerHTML = value.text;
                s.add(option);
            });
        }
    }

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

    button.onclick = function () {
        input.click();
    };

    input.onchange = function () {

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

function fileName(name) {
    return name.split('/').pop().split('\\').pop();
}

Array.prototype.remove = function () {
    var what, a = arguments, L = a.length, ax;
    while (L && this.length) {
        what = a[--L];
        while ((ax = this.indexOf(what)) !== -1) {
            this.splice(ax, 1);
        }
    }
    return this;
};