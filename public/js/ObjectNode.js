
function ObjectNode() {
    this.formLoader = function (form) {
        var cCheck = form.querySelector(".loading-pro");
        if (cCheck) cCheck.remove();

            var loading = document.createElement("div");
            $(loading).addClass("formLoader").addClass("loading-pro");
            var topBar = document.createElement("div");
            topBar.className = "load-bar absolute top-left";
            topBar.innerHTML = "<div class=\"bar\"></div><div class=\"bar\"></div><div class=\"bar\"></div><div class=\"bar\"></div>";
            loading.appendChild(topBar);
            var loader2 = document.createElement("div");
            loader2.className = "loader-new didHide";
            loading.appendChild(loader2);
            setInterval(function (res) {
                $(topBar).slideToggle();
                $(loader2).slideToggle();
            }, 9000);
            var innerLoad = document.createElement("div");
            $(innerLoad).addClass("formLoading");
            loading.appendChild(innerLoad);
            var hide = function(){
                $(loading).fadeOut();
            };

            form.insertBefore(loading, form.firstChild);

            $(loading).fadeIn();

            return {
                loader:loading,
                hide:hide
            };

    };

    this.menuButton = function () {
        var elem = document.createElement("div");
        elem.id = "tour-fullwidth";
        $(elem).addClass("navbar-btn").html("<button type=\"button\" class=\"btn-toggle-fullwidth\"><i class=\"ti-arrow-circle-left\"></i></button>");

        return {
            elem:elem,
            hide:function () {
                $(this.elem).hide();
            }
        }
    };

    this.topSearchForm = function () {
        var form = document.createElement("form");

        $(form).addClass("navbar-form navbar-left search-form").html("<input type=\"text\" value=\"\" class=\"form-control\" placeholder=\"Search dashboard...\">\n" +
            "\t\t\t\t\t\t<button type=\"button\" class=\"btn btn-default\"><i class=\"fa fa-search\"></i></button>");

        return {
            elem:form,
            hide:function () {
                $(this.elem).hide();
            }
        }
    };


    this.menu = function ( menuList ) {
        var menu = document.createElement("div");
        $(menu).addClass("navbar-menu");

        var ul = document.createElement("ul");
        $(ul).addClass("nav navbar-nav navbar-right");

        this.menuList(menuList,ul,true);

        menu.appendChild(ul);

        return {
            elem:menu,
            ul:ul,
            remove:function () {
                this.ul.remove();
            }
        }
    }
}