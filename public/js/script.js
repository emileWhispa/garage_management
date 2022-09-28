window.onload = function (ev) {

    var main = new Controller();


    var r = new XMLHttpRequest();
    r.open("GET",document.location,false);
    r.setRequestHeader('X-Requested-With', 'XMLHttpRequest');

    r.send();


    main.loginOr(r);



};