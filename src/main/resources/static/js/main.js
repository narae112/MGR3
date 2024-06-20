/*
Template Name: EventGrids - Conference and Event HTML Template.
Author: GrayGrids
*/

(function () {
    //===== Prealoder

//    window.onload = function () {
//        window.setTimeout(fadeout, 500);
//    }
//
//    function fadeout() {
//        document.querySelector('.preloader').style.opacity = '0';
//        document.querySelector('.preloader').style.display = 'none';
//    }


    /*=====================================
    Sticky
    ======================================= */
document.addEventListener("DOMContentLoaded", function () {
    window.onscroll = function () {
        var nav_logo = document.querySelector(".nav_logo");
        var nav_logo_color = document.querySelector(".nav_logo_color");

        if (window.pageYOffset === 0) { // 스크롤이 0일 때
            nav_logo_color.style.display = "none";
            nav_logo.style.display = "block";
        } else {
            nav_logo.style.display = "none";
            nav_logo_color.style.display = "block";
        }

        var header_navbar = document.querySelector(".navbar-area.main");
        var sticky = header_navbar.offsetTop;

        if (window.pageYOffset > sticky) {
            header_navbar.classList.add("sticky");

        } else {
            header_navbar.classList.remove("sticky");
        }

        var backToTo = document.querySelector(".scroll-top");

        if (document.body.scrollTop > 50 || document.documentElement.scrollTop > 50) {
            backToTo.style.display = "flex";
        } else {
            backToTo.style.display = "none";
        }

    };
});

    // WOW active
    new WOW().init();

    //===== mobile-menu-btn
    document.addEventListener('DOMContentLoaded', function () {
        let navbarToggler = document.querySelector(".mobile-menu-btn");
        if (navbarToggler) {
            navbarToggler.addEventListener('click', function () {
                navbarToggler.classList.toggle("active");
            });
        }
    });

})();