<%--@author AmilaS--%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
<meta http-equiv="content-type" content="text/html; charset=utf-8"/>
<title>Word Analyzer</title>

<link href="${pageContext.request.contextPath}/resources/css/trs_console.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/resources/css/monitor_table.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/resources/css/jquery-ui-1.8.4.custom.css" rel="stylesheet"
      type="text/css"/>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui.min.js"></script>

<script src="${pageContext.request.contextPath}/resources/js/jquery.jpanelmenu.min.js" type="text/javascript"></script>
<!--[if lte IE 8]>
<script language="javascript" type="text/javascript" src="/resources/js/flot/excanvas.min.js"></script><![endif]-->
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.min.js"></script>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.pie.min.js"></script>

<script type="text/javascript">
$(document).ready(function () {
    var jPM = $.jPanelMenu({
        menu: 'header.main nav',
        animated: true
    });
    jPM.on();
    $("#loadingImage").hide();
    $("#subtitle").html("All Sessions")
});
$(function () {
    $("#tabs").tabs();
    $("#refreshToday").button();
    $("#refreshYesterday").button();
    $("#refreshWeek").button();
    $("#refreshMonth").button();
    $(popup_close).button();
    $(view_messages).button();

});

$(document).ready(function () {
    var $lmTable = $("#information").dataTable({
        "sPaginationType": "full_numbers",
        "sScrollY": calcDataTableHeight(),
        "sSearch": "Search all:",
        "iDisplayLength": 100,
        "aaSorting": [
            [0, 'desc']
        ],
        //"bJQueryUI": true
        "aaData": [],
        "aoColumns": [
            { "mDataProp": "startTime", sDefaultContent: "n/a"},
            { "mDataProp": "endTime", sDefaultContent: "n/a"},
            { "mDataProp": "duration", sDefaultContent: "n/a"},
            { "mDataProp": "sessionId", sDefaultContent: "n/a"},
            { "mDataProp": "cost", sDefaultContent: "n/a"},
            { "mDataProp": "clientType", sDefaultContent: "n/a"},
            { "mDataProp": "channelId", sDefaultContent: "n/a"},
            { "mDataProp": "clientVersion", sDefaultContent: "n/a"},
            { "mDataProp": "userId", sDefaultContent: "n/a"},
            { "mDataProp": "loginAlias", sDefaultContent: "n/a"},
            { "mDataProp": "endReason", sDefaultContent: "n/a"}
        ]
    });

    var $searchBar = $("tfoot input");

    $searchBar.keyup(function () {
        /* Filter on the column (the index) of this element */
        $lmTable.fnFilter(this.value, $("tfoot input").index(this));
    });

    var asInitVals = [];
    // Support functions to provide user friendlyness' to the textboxes in the footer
    $searchBar.each(function (i) {
        asInitVals[i] = this.value;
    });
    $searchBar.focus(function () {
        if (this.className == "search_init") {
            this.className = "";
            this.value = "";
        }
    });
    $searchBar.blur(function (i) {
        if (this.value == "") {
            this.className = "search_init";
            this.value = asInitVals[$("tfoot input").index(this)];
        }
    });

    $(document).on("click", "#popup_close", function () {
        $('#mask').hide();
        $('.popupWindow').hide();
    });

    //if mask is clicked
    $('#mask').click(function () {
        $(this).hide();
        $('.popupWindow').hide();
    });

    $(window).resize(function () {
        var box = $('#boxes .popupWindow');
        //Get the screen height and width
        var maskHeight = $(document).height();
        var maskWidth = $(window).width();
        //Set height and width to mask to fill up the whole screen
        $('#mask').css({'width': maskWidth, 'height': maskHeight});
        //Get the window height and width
        var winH = $(window).height();
        var winW = $(window).width();
        //Set the popup window to center
        box.css('top', winH / 2 - box.height() / 2);
        box.css('left', winW / 2 - box.width() / 2);
    });

    refreshSessions('data/allSessions', '');

    var msgWindow = $('#sessionDialog');
    var box = $('#boxes .popupWindow');
    var winH = $(window).height();
    var winW = $(window).width();
    //Set the popup window to center
    $(box).css('top', winH / 2 - $(msgWindow).height() / 2);
    $(box).css('left', winW / 2 - $(msgWindow).width() / 2);

    refreshTime();
});

var calcDataTableHeight = function () {
    var h = Math.floor($(window).height() * 55 / 100);
    return h + 'px';
};
$(window).resize(function () {
    var oTable = $("#information").dataTable();
    $('div.dataTables_scrollBody').css('height', calcDataTableHeight());
    oTable.fnAdjustColumnSizing();
});

$(document).on("click", "#today", function () {
    refreshSessions('data/allSessions', 'today');
});
$(document).on("click", "#refreshToday", function () {
    refreshSessions('data/allSessions', 'today');
});
$(document).on("click", "#yesterday", function () {
    refreshSessions('data/allSessions', 'yesterday');
});
$(document).on("click", "#refreshYesterday", function () {
    refreshSessions('data/allSessions', 'yesterday');
});
$(document).on("click", "#week", function () {
    refreshSessions('data/allSessions', 'week');
});
$(document).on("click", "#refreshWeek", function () {
    refreshSessions('data/allSessions', 'week');
});
$(document).on("click", "#month", function () {
    refreshSessions('data/allSessions', 'month');
});
$(document).on("click", "#refreshMonth", function () {
    refreshSessions('data/allSessions', 'month');
});


function refreshTime() {
    $.get("data/serverTime", function (response) {
        $("#server-time-text").html(response[0] + "<br/>" + response[1]);
    });
    setTimeout(refreshTime, 60000);
}

function refreshSessions(url, dataParam) {
    $('#information').hide();
    $('#loadingImage').show();
    $.get(url, {date: dataParam}, function (responseJson) {
        $('#information').show();
        $('#loadingImage').hide();
        refreshTable(responseJson)
    });
}

var $lmTable;

function refreshTable(responseJson) {
    $lmTable = $("#information").dataTable();
    $lmTable.fnClearTable(this);
    $.each(responseJson, function (index, session) {
        $lmTable.fnAddData(session, false);
    });
//            oSettings.aiDisplay = oSettings.aiDisplayMaster.slice();
    $lmTable.fnDraw(this);
}

$(document).on('click', '#information tbody tr', function () {
    var msgWindow = $('#sessionDialog');
    var aData = $lmTable.fnGetData(this);
    var dataPie = [
        {label: "Loading...", data: 1}
    ];
    $.plot($("#flotPlaceholder"), dataPie, {
        series: {
            pie: {show: true,
                innerRadius: 0.5,
                label: {
                    show: true,
                    radius: 0.001
                }
            }
        },
        legend: {show: false}
    });
    $("#dialog_session").html(aData.sessionId);
    $("#view_messages").attr("href", "session-messages?sessionId=" + aData.sessionId);
    $("#dialog_client").html(aData.clientType);
    $("#dialog_login").html(aData.loginAlias);
    $("#dialog_userId").html(aData.userId);
    $("#dialog_start").html(aData.startTime);
    $("#dialog_end").html(aData.endTime);
    $("#dialog_duration").html(aData.duration);
    $("#dialog_reason").html(aData.endReason);

    $.get("data/countMessagesPerSessionByGroup", {sessionId: aData.sessionId}, function (responseJson) {
        var dataPie = [
            {label: "Trade", data: responseJson[0]},
            {label: "Trading Inquiry", data: responseJson[1]},
            {label: "Inquiry", data: responseJson[2]},
            {label: "Finance", data: responseJson[3]},
            {label: "Auth", data: responseJson[4]},
            {label: "System", data: responseJson[5]},
            {label: "Cust.Inquiry", data: responseJson[6]},
            {label: "Subscription", data: responseJson[7]}
        ];

        $.plot($("#flotPlaceholder"), dataPie, {
            series: {
                pie: {show: true,
                    innerRadius: 0.3,
                    label: {
                        show: true
                    },
                    combine: {
                        threshold: 0.02
                    }
                }
            },
            legend: {show: false},
            grid: {
                hoverable: true
            }
        });

        showMask();
    });

    $.get("data/countMessagesPerSession", {sessionId: aData.sessionId}, function (responseJson) {

        $("#dialog_msg_count").html(responseJson)
    });

//transition effect
    $(msgWindow).fadeIn(600);

})
;

function showMask() {
    var msgWindow = $('#sessionDialog');
    //Get the screen height and width
    var maskHeight = $(document).height();
    var maskWidth = $(window).width();
    //Set height and width to mask to fill up the whole screen
    var mask = $('#mask').css({'width': maskWidth, 'height': maskHeight});
    //transition effect
    mask.fadeIn(300);
    mask.fadeTo("slow", 0.8);
    var box = $('#boxes .popupWindow');
    //Get the window height and width
    var winH = $(window).height();
    var winW = $(window).width();
    //Set the popup window to center
    $(box).css('top', winH / 2 - $(msgWindow).height() / 2);
    $(box).css('left', winW / 2 - $(msgWindow).width() / 2);

}

</script>

<style type="text/css">
    #mask {
        position: absolute;
        left: 0;
        top: 0;
        z-index: 9000;
        background-color: #000;
        display: none;
    }

    #boxes .popupWindow {
        position: fixed;
        left: 0;
        top: 0;
        max-width: 80%;
        max-height: 80%;
        display: none;
        z-index: 9999;
        padding: 20px;
    }

    #boxes #sessionDialog {
        /*padding:50px 0 20px 25px;*/
        padding: 10px;
        background-color: #ffffff;
        overflow: auto;
    }

</style>

</head>
<body id="all_session_body">

<%@include file="menu.jsp" %>

<div class="content" id="container" style="width: 98%; margin: auto">
    <h2>All Sessions</h2>

    <div id="tabs">
        <ul>
            <li id="today"><a href="#tabs-1">Today</a></li>
            <li id="yesterday"><a href="#tabs-4">Yesterday</a></li>
            <li id="week"><a href="#tabs-2">This Week</a></li>
            <li id="month"><a href="#tabs-3">This Month</a></li>
        </ul>
        <div id="tabs-1">
            <label id="refreshToday">Refresh</label>
        </div>
        <div id="tabs-4">
            <label id="refreshYesterday">Refresh</label>
        </div>
        <div id="tabs-2">
            <label id="refreshWeek">Refresh</label>
        </div>
        <div id="tabs-3">
            <label id="refreshMonth">Refresh</label> <br/>
        </div>

        <div id="session_info">

            <div id="loadingImage" align="center">
                <img src="${pageContext.request.contextPath}/resources/images/ajax-loader.gif" alt="Loading..."/>
            </div>

            <table id="information" class="display">
                <thead>
                <tr>
                    <th>START TIME</th>
                    <th>END TIME</th>
                    <th>DURATION</th>
                    <th>SESSION ID</th>
                    <th>COST</th>
                    <th>CLIENT TYPE</th>
                    <th>CHANNEL ID</th>
                    <th>CLIENT VERSION</th>
                    <th>USER ID</th>
                    <th>LOGIN ALIAS</th>
                    <th>END REASON</th>
                </tr>
                </thead>
                <tbody id="infoBody">
                </tbody>
                <tfoot>
                <tr class="search_bar">
                    <th><input type="text" name="search_start_time" value="Search start_time" class="search_init"/></th>
                    <th><input type="text" name="search_end_time" value="Search end_time" class="search_init"/></th>
                    <th><input type="text" name="search_duration" value="Search duration" class="search_init"/></th>
                    <th><input type="text" name="search_session" value="Search session" class="search_init"/></th>
                    <th><input type="text" name="search_cost" value="Search cost" class="search_init"/></th>
                    <th><input type="text" name="search_client" value="Search client" class="search_init"/></th>
                    <th><input type="text" name="search_channel" value="Search client" class="search_init"/></th>
                    <th><input type="text" name="search_version" value="Search version" class="search_init"/></th>
                    <th><input type="text" name="search_userId" value="Search userId" class="search_init"/></th>
                    <th><input type="text" name="search_login" value="Search loginAlias" class="search_init"/></th>
                    <th><input type="text" name="search_endReason" value="Search end_reason" class="search_init"/></th>
                </tr>
                </tfoot>
            </table>
        </div>

    </div>

    <div id="boxes">
        <div id="sessionDialog" class="popupWindow">
            <table>
                <tr>
                    <td>
                        <label>Session: </label>
                        <span id="dialog_session"></span> <br/>
                        <label>Total messages: </label>
                        <span id="dialog_msg_count"></span> <br/>
                        <label>Client Type: </label>
                        <span id="dialog_client"></span> <br/>
                        <label>Login Alias: </label>
                        <span id="dialog_login"></span> <br/>
                        <label>User Id:</label>
                        <span id="dialog_userId"></span> <br/>
                        <label>Start time: </label>
                        <span id="dialog_start"></span> <br/>
                        <label>End time: </label>
                        <span id="dialog_end"></span> <br/>
                        <label>Duration:</label>
                        <span id="dialog_duration"></span> <br/>
                        <label>End Reason:</label>
                        <span id="dialog_reason"></span> <br/>
                    </td>
                    <td>
                        <label>Message Distribution:</label>

                        <div id="flotPlaceholder" style="width:300px;height:300px;"></div>
                    </td>

                </tr>
            </table>
            <a id="view_messages">View all messages</a>
            <label id="popup_close">Close</label>
        </div>
        <!-- Mask to cover the whole screen -->
        <div id="mask"></div>
    </div>

</div>

</body>
</html>