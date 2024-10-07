$(document).ready(function (){
    $("#logoutLink").on("click", function(e){
        e.preventDefault();
        document.logoutForm.submit();
    });

    customizeDropdownMenu();
    customizeTabs();
});

function customizeDropdownMenu(){
    $('.navbar .dropdown').hover(
        function(){
            $(this).find('.dropdown-menu').first().stop(true, true).delay(250).slideDown();
        },
        function(){
            $(this).find('.dropdown-menu').first().stop(true, true).delay(100).slideUp();
        }
    )
    $('.dropdown > a').on('click', function(){
        location.href = this.href;
    });
}

function customizeTabs() {
    let url = document.location.toString();
    if (url.match('#')) {
        $('.nav-tabs a[href="#' + url.split('#')[1] + '"]').tab('show');
    }

    $('.nav-tabs a').on('shown.bs.tab', function (e) {
        window.location.hash = e.target.hash;
    });
}

function formatBytes(a,b=2){if(!+a)return"0 Bytes";const c=0>b?0:b,d=Math.floor(Math.log(a)/Math.log(1024));return`${parseFloat((a/Math.pow(1024,d)).toFixed(c))}${["Bytes","KB","MB","GB","TB","PB","EB","ZB","YB"][d]}`}