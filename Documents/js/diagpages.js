$(document).ready(function() {
    $("#rfrshBtn").click(function() {
        window.location.reload(true);
    });

    $("#goHomeBtn").click(function() {
        window.history.back();
    });
});

$("body").flowtype({
    minFont: 8,
    maxFont: 14,
    fontRatio: 35
});
