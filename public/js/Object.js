function MenuController() {
    this.wrapper = null;
    this.deposit = null;
    var self = this;
    this.node = new ObjectNode();

    this.menuJson = {};

    this.baseLoad = function () {
        $("html").addClass("formLoading");
    };

    this.hideBaseLoad = function () {
        $("html").removeClass("formLoading");
    };

    this.resetPage = function () {
      $(this.deposit).empty();
    };

    this.navBrand = function () {
        var brand = document.createElement("div");
        $(brand).addClass("brand");
            //.html("<a href=\"index.html\">\n" +
            //"\t\t\t\t\t\t<img src=\"assets/images/logo-white.png\" alt=\"Pro Logo\" class=\"img-responsive logo\">\n" +
            //"\t\t\t\t\t</a>");

        return {
            elem:brand,
            hide:function () {
                $(this.elem).hide();
            }
        }
    };

    this.headerFluid = function () {
        var element = document.createElement("div");
        $(element).addClass("container-fluid");

        return {
            elem:element,
            button:self.node.menuButton(),
            form:self.node.topSearchForm(),
            menu:self.node.menu(self.menuJson["TopMenu"]),
            hide:function () {
                $(this.elem).hide();
            },
            get:function () {
                this.elem.appendChild(this.button.elem);
                this.elem.appendChild(this.form.elem);
                this.elem.appendChild(this.menu.elem);
                return this.elem;
            }
        }
    };

    this.navHeader = function ( tit ) {
        var element = document.createElement("header");
        $(element).addClass("main-header light-shadow");

        element.innerHTML = "<a href=\"#\" class=\"logo hidden-xs\"><span class=\"logo-lg\"><img src=\""+tit+"\" width=\"40\"></span></a><nav class=\"navbar navbar-static-top\"><a href=\"#\" class=\"sidebar-toggle\" data-toggle=\"push-menu\" role=\"button\">\n" +
            "                <span class=\"sr-only\">Toggle navigation</span>\n" +
            "            </a><div class=\"navbar-custom-menu\">\n" +
            "            <ul class=\"nav navbar-nav\"></ul>\n" +
            "        </div></nav>";

        return {
            elem:element,
            brand:self.navBrand(),
            addBrand:function(){
                //this.elem.appendChild(this.brand.elem);
            },
            addFluid:function(){
                this.elem.appendChild(this.container.get());
            },
            toWrapper:function () {
                document.body.appendChild(this.elem);
            }
        }
    };

    this.leftMenu = function ( ctrl , json ) {
        var element = document.createElement("aside");
        element.className = "main-sidebar myScroll";
        $(element).addClass("sidebar");

        var bar = document.createElement("section");
        bar.className = "sidebar";

        var uRole = document.createElement("div");
        uRole.className = "user-panel";
        uRole.innerHTML = " <div class=\"pull-left\">\n" +
            "                <div class=\"round-image\">\n" +
            "                    <img src=\""+json.icon+"\" alt=\"User Image\">\n" +
            "                </div>\n" +
            "            </div>\n" +
            "            <div class=\"pull-left info\">\n" +
            "                <p>"+json.user.email+"</p>\n" +
            "                <a href=\"#\"><i class=\"fa fa-circle text-success\"></i> Role title</a>\n" +
            "            </div>";




        var list = document.createElement("ul");
        list.className = "sidebar-menu";
        $(list).attr("data-widget","tree");

        element.appendChild(bar);
        bar.appendChild(uRole);
        bar.appendChild(list);

        var wr = this.deposit;

        this.resetPage();

        return {
            elem:element,
            list:list,
            controller:ctrl,
            array:[],
            liArray:[],
            addMenu:function( m , k , uList ){
                var li = document.createElement("li");
                var a = document.createElement("a");
                var cTab = document.createElement("div");
                cTab.className = "t-content relative";
                wr.appendChild(cTab);

                $(cTab).addClass("hidden");

                var header = document.createElement("div");
                var body = document.createElement("div");
                var h1 = document.createElement("h1");

                cTab.appendChild(header);
                cTab.appendChild(body);


                var o = {};
                o.tab = cTab;
                o.data = body;
                o.header = header;
                o.link = a;
                o.hTitle = h1;
                o.liElement = li;
                o.addHeader = function ( t ) {
                    this.hTitle.textContent = t;
                    this.header.appendChild(this.hTitle);
                };

                this.array.push(o);


                this.liArray.push(li);
                var sp = document.createElement("span");
                sp.innerHTML = m.title;
                var iP = document.createElement("i");
                iP.className = m.icon;
                a.appendChild(iP);
                a.appendChild(sp);

                li.appendChild(a);
                if( m.menuList.length ){
                    a.href = "#";
                    $(li).addClass("treeview");
                    var list = document.createElement("ul");
                    $(list).addClass("treeview-menu");
                    var ax = this;
                    m.menuList.forEach(function (val,k) {
                        ax.addMenu(val,k,list);
                    });

                    a.onclick = function () {
                        $(list).slideToggle();
                    };

                    li.appendChild(list);
                }
                else{
                    a.href = m.href;
                    o.click = function () {

                        $(arr).removeClass("active");
                        $(li).addClass("active");

                        oArray.forEach(function (value) {
                            $(value.tab).addClass("hidden");
                        });
                        $(cTab).removeClass("hidden");

                        if( m.redirect ) return true;
                        else if( m.js ){
                            $(body).empty();
                            switch (m.type){
                                case "g_app":{
                                    spareApprove(ctrl,o);
                                    break;
                                }
                                case "summary":{
                                    stockSummary(ctrl,o);
                                    break;
                                }
                                case "load-profile":{
                                    profileUpdate(ctrl,o);
                                    break;
                                }
                                case "tap_edit":{
                                    editProf(ctrl,o);
                                    break;
                                }
                                case "g_old":{
                                    oldSpareGApprove(ctrl,o);
                                    break;
                                }
                                case "s_report":{
                                    doReport(ctrl,o);
                                    break;
                                }
                                case "s_x_report":{
                                    quickReport(ctrl,o);
                                    break;
                                }
                                case "o_app":{
                                    oldSpareApprove(ctrl,o);
                                    break;
                                }
                                case "role":{
                                    changeRole(ctrl,o);
                                    break;
                                }
                                case "add_to_s":{
                                    addToStock(ctrl,o);
                                    break;
                                }
                                case "sign-out":{
                                    signOut(ctrl,o);
                                    break;
                                }
                            }
                            return false;
                        }

                        o.addHeader(m.title);

                        ctrl.request({
                            url:m.href,
                            success:function (res) {
                                $(body).empty();
                                var json = ctrl.jsonParse(res);
                                var tab = new Tabs(json);
                                body.appendChild(tab.get());
                            }
                        });
                        return false;
                    };

                    a.onclick = o.click;
                }

                var arr = this.liArray;
                var oArray = this.array;



                if( !k ) a.click();

                uList.appendChild(li);
            },
            toWrapper:function ( obj ) {
                var ax = this;
                var l = this.list;
                obj.forEach(function (value,k) {
                    ax.addMenu(value,k,l);
                });
                document.body.appendChild(this.elem);
            }
        }
    };

    this.homePage = function ( json , ctrl ) {
        this.resetPage();
        var header = this.navHeader( json.logo );
        header.toWrapper();

        var leftMenu = this.leftMenu(ctrl,json);
        leftMenu.toWrapper(json.menu);


        this.wrapperToBody();


        var chat = new Chat();

        chat.webSocketUrl = ctrl.mainRequest.getResponseHeader("socket");
        chat.user = json.user;

        chat.start();


    };


    this.isFunction = function (callback) {
        return callback !== undefined && typeof callback === "function";
    };

    this.checkLength = function (o, min, max) {
        var valid = !(o > max || o < min);
        var message = "Require length " + min + " to " + max;

        return {
            valid: valid,
            message: message
        };
    };

    this.checkRex = function (o, rex, msg) {
        var valid = rex.test(o);
        var message = msg === undefined ? "Require field error" : msg;

        return {
            valid: valid,
            message: message
        };
    };

    this.validate = function (input, el) {
        var valid = true, minL = el.minLength !== undefined ? el.minLength : 1;
        var value = el.noTrim !== undefined ? input.value : input.value.trim();
        var emailMessage = "Enter Valid Email eg: js@study.web";

        var cLength = this.checkLength(value.length, minL, 1000);
        var emailRex = this.checkRex(value, /^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]+$/i, emailMessage);

        valid = valid && cLength.valid;

        if (el.email !== undefined) valid = valid && emailRex.valid;

        var msg = cLength.valid ? emailMessage : cLength.message;


        return {
            valid: valid,
            message: msg
        };
    };

    this.validation = function (group, label, input, elem) {
        $(group).find(".error-tool").remove();
        var labelNew = document.createElement("span");
        var icon = document.createElement("i");
        icon.className = "text-red fa fa-warning";
        labelNew.className = "error-tool";
        labelNew.appendChild(icon);
        var context = document.createElement("span");
        labelNew.appendChild(context);
        var valid = self.validate(input, elem);
        if (valid.valid) {
            labelNew.remove();
            $(group).removeClass("has-error");
        }
        else {
            context.innerHTML = " " + valid.message;
            group.appendChild(labelNew);
            $(labelNew).animate({
                width: 0
            }, 400).animate({
                width: "100%"
            }, "slow");
            $(group).addClass("has-error");
        }

        return valid;
    };

    this.handleEvent = function (group, label, input, elem, callback) {
        input.onkeyup = function () {
            self.validation(group, label, input, elem);
            if (self.isFunction(callback)) callback(group, label, input, elem);
        };
    };

    this.request = function (obj) {

        var type = obj.method !== undefined ? obj.method : "GET";
        var url = obj.url !== undefined ? obj.url : "/";

        var xhr = new XMLHttpRequest();

        xhr.open(type, url, true);

        xhr.onload = function () {
            if (xhr.status >= 200 && xhr.status < 400) {

                if (self.isFunction(obj.success)) obj.success(xhr.responseText, xhr.getAllResponseHeaders(), xhr.status);

            } else {
                if (self.isFunction(obj.error)) obj.error(xhr.responseText, xhr.getAllResponseHeaders(), xhr.status);
            }
        };

        if (type === "POST" && obj.form !== undefined) {
            var formData = new FormData(obj.form);
            xhr.send(formData);
        } else xhr.send();


    };


    this.stackForm = function (json, callback) {
        var form = document.createElement("div");
        form.className = "form-group";
        var array = [];
        var click = callback !== undefined && this.isFunction(callback.onClick);
        var create = callback !== undefined && this.isFunction(callback.onCreate);
        Array.prototype.forEach.call(json, function (el, i) {

            var group = document.createElement("div");
            group.className = "form-group relative";
            var label = document.createElement("label");
            label.innerHTML = el.label;
            var input = document.createElement("input");
            input.autocomplete = "off";
            input.type = el.type;
            input.value = el.value;
            input.name = el.name;
            input.placeholder = el.label;
            input.className = "form-control";
            input.onclick = function (ev) {
                $(array).removeClass("active");
                $(this.parentNode).addClass("active");
                if (click) callback.onClick(this, array, el, ev);
            };

            var newArray = {
                group: group,
                label: label,
                input: input,
                elem: el
            };

            group.appendChild(label);
            array.push(newArray);

            self.handleEvent(group, label, input, el);


            form.appendChild(group);


            if (create) callback.onCreate(group, input, el, i);
        });
        return {
            form: form,
            array: array
        };
    };

    this.validateForm = function (array, form) {
        var valid = true;
        Array.prototype.forEach.call(array, function (el) {
            var value = self.validation(el.group, el.label, el.input, el.elem);
            valid = valid && value.valid;
        });
        if (valid) {
            var loader = self.node.formLoader(form);
        }
        return {
            valid: valid,
            loader: loader
        }
    };

    this.newForm = function (obj) {
        var valid = obj !== undefined;
        var title = valid && obj.title !== undefined ? obj.title : "Update form";
        var content = valid && obj.content !== undefined && typeof obj.content === "object" ? obj.content : document.createElement("div");
        var solid = document.createElement("div");
        solid.className = "box box-primary relative";
        var header = document.createElement("div");
        header.className = "box-header with-border";
        header.innerHTML = "<h3 class=\"box-title\"><i class=\"fa fa-feed\"></i> " + title + "</h3>";
        solid.appendChild(header);

        var form = document.createElement("form");
        form.action = obj.route;
        form.method = "POST";
        //var isCheck = obj.isCheck !== undefined && obj.isCheck;
        var body = document.createElement("div");

        form.appendChild(body);
        body.className = "box-body";

        var errorPlace = document.createElement("div");
        errorPlace.className = "form-error-place absolute top-left bg-danger five-pad";
        body.appendChild(errorPlace);

        $(errorPlace).hide();

        var error = {
            elem:errorPlace,
            putError:function (error,time) {
                var elem = this.elem;
                $(elem).hide().slideToggle().addClass("text-white").html(error);

                var t = time !== undefined ? time : 10000;
                setTimeout(function () {
                    $(elem).slideToggle();
                },t);
            }
        };


        form.onsubmit = function (e) {
            e.preventDefault();
            var rNode = self.validateForm(obj.groupArray, this);
            var check = self.isFunction(obj.callback);
            if (check && rNode.valid) {
                obj.callback(this, rNode.loader,error);
            }
        };

        body.appendChild(content);
        solid.appendChild(form);

        var footer = document.createElement("div");
        footer.className = "box-footer clearfix";
        var largeClass = obj.buttonLarge !== undefined ? "btn-block" : "";
        footer.innerHTML = "<button type=\"submit\" class=\"pull-right btn btn-primary " + largeClass + "\">" + obj.buttonText + " <i class=\"fa fa-arrow-circle-right\"></i></button>";
        form.appendChild(footer);

        return solid;
    };

    this.init = function () {
        var wrapper = document.createElement("div");
        wrapper.className = "content-wrapper";
        this.wrapper = wrapper;

        var d = document.createElement("div");
        d.className = "five-pad";
        this.deposit = d;


        var div = document.createElement("div");
        div.className = "progress-line";
        //this.wrapper.appendChild(div);
        this.wrapper.appendChild(d);
    };

    this.wrapperToBody = function () {
        document.body.appendChild(this.wrapper);
        document.documentElement.style.height = "100%";
        document.body.style.height = "100%";
    };

    this.init();
}