<%--
  @author: AmilaS
  Date: 1/19/14
  Time: 7:00 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<title></title>
<script src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.min.js" type='text/javascript'></script>
<!--[if lte IE 8]>
<script language="javascript" type="text/javascript" src="/resources/js/flot/excanvas.min.js"></script><![endif]-->

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.min.js"></script>
<script type="text/javascript"
        src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.barnumbers.js"></script>
<link href="${pageContext.request.contextPath}/resources/css/trs_console.css" rel="stylesheet" type="text/css"/>
<link href="${pageContext.request.contextPath}/resources/css/jquery-ui-1.8.4.custom.css" rel="stylesheet"
      type="text/css" media="all"/>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui.min.js"></script>

<script src="${pageContext.request.contextPath}/resources/js/jquery.jpanelmenu.min.js" type="text/javascript"></script>

<script type="text/javascript">
    $(document).ready(function () {
        var jPM = $.jPanelMenu({
            menu: 'header.main nav',
            animated: true
        });
        jPM.on();
//        jPM.open();
        $('#loadingDiv').hide();
        fetchData('1', "today", $("#flot-placeholder-trade"));
    });

    $(function () {
        $("#tabs").tabs({heightStyle: "fill"}).addClass("ui-tabs-vertical ui-helper-clearfix");
        $("#tabs li").removeClass("ui-corner-top").addClass("ui-corner-left");
    });
    $(function () {
        $("#radio-trade").buttonset();
        $("#radio-trading-inquiry").buttonset();
        $("#radio-inquiry").buttonset();
        $("#radio-finance").buttonset();
        $("#radio-auth").buttonset();
        $("#radio-system").buttonset();
        $("#radio-cust-inquiry").buttonset();
        $("#radio-subscription").buttonset();
    });
</script>

<style>
    .ui-tabs-vertical {
        width: 55em;
    }

    .ui-tabs-vertical .ui-tabs-nav {
        padding: .2em .1em .2em .2em;
        float: left;
        width: 12em;
    }

    .ui-tabs-vertical .ui-tabs-nav li {
        clear: left;
        width: 100%;
        border-bottom-width: 1px !important;
        border-right-width: 0 !important;
        margin: 0 -1px .2em 0;
    }

    .ui-tabs-vertical .ui-tabs-nav li a {
        display: block;
    }

    .ui-tabs-vertical .ui-tabs-nav li.ui-tabs-active {
        padding-bottom: 0;
        padding-right: .1em;
        border-right-width: 1px;
    }

    .ui-tabs-vertical .ui-tabs-panel {
        padding: 1em;
        float: left;
        width: 40em;
    }
</style>

<script type="text/javascript">

var options = {
    series: {
        bars: {
            show: true,
            numbers: {
                show: true
            }
        }
    },
    bars: {
        align: "center",
        barWidth: 0.5,
        horizontal: true,
        fillColor: { colors: [
            { opacity: 0.5 },
            { opacity: 1}
        ] },
        lineWidth: 1
    },
    xaxis: {
//        transform: function (v) {
//            return Math.log(v + 0.0001);
        /*move away from zero*/
//        },
//        tickFormatter: function (v, axis) {
//            return "10" + (Math.round(Math.log(v) / Math.LN10)).toString().sup();
//        },
        axisLabel: "Count",
        axisLabelUseCanvas: false,
        axisLabelFontSizePixels: 12,
        axisLabelFontFamily: 'Verdana, Arial',
        axisLabelPadding: 10,
        tickColor: "#5E5E5E",
        color: "black"
    },
    yaxis: {
        axisLabel: "Request Type",
        axisLabelUseCanvas: true,
        axisLabelFontSizePixels: 12,
        axisLabelFontFamily: 'Verdana, Arial',
        axisLabelPadding: 3,
        tickColor: "#5E5E5E",
        ticks: [],
        color: "black"
    },
    legend: {
        show: false
//            noColumns: 0,
//            labelBoxBorderColor: "#858585",
//            position: "ne"
    },
    grid: {
        hoverable: true,
        borderWidth: 2,
        backgroundColor: { colors: ["#171717", "#4F4F4F"] }
    }
};


$(document).on("click", "#tabs-a1", function () {
    fetchData('1', $('input[name="radio-trade"]:checked').val(), $("#flot-placeholder-trade"));
});

$(document).on("click", "#tabs-a2", function () {
    fetchData('2', $('input[name="radio-trading-inquiry"]:checked').val(), $("#flot-placeholder-trading-inquiry"));
});

$(document).on("click", "#tabs-a3", function () {
    fetchData('3', $('input[name="radio-inquiry"]:checked').val(), $("#flot-placeholder-inquiry"));
});

$(document).on("click", "#tabs-a4", function () {
    fetchData('4', $('input[name="radio-finance"]:checked').val(), $("#flot-placeholder-finance"));
});

$(document).on("click", "#tabs-a5", function () {
    fetchData('5', $('input[name="radio-auth"]:checked').val(), $("#flot-placeholder-auth"));
});

$(document).on("click", "#tabs-a6", function () {
    fetchData('6', $('input[name="radio-system"]:checked').val(), $("#flot-placeholder-system"));
});

$(document).on("click", "#tabs-a7", function () {
    fetchData('10', $('input[name="radio-cust-inquiry"]:checked').val(), $("#flot-placeholder-cust-inquiry"));
});

$(document).on("click", "#tabs-a8", function () {
    fetchData('11', $('input[name="radio-subscription"]:checked').val(), $("#flot-placeholder-subscription"));
});


$(document).on("click", "#trade-today", function () {
    fetchData('1', "today", $("#flot-placeholder-trade"));
});

$(document).on("click", "#trade-today", function () {
    fetchData('1', "today", $("#flot-placeholder-trade"));
});
$(document).on("click", "#trade-yesterday", function () {
    fetchData('1', "yesterday", $("#flot-placeholder-trade"));
});
$(document).on("click", "#trade-week", function () {
    fetchData('1', "week", $("#flot-placeholder-trade"));
});
$(document).on("click", "#trade-month", function () {
    fetchData('1', "month", $("#flot-placeholder-trade"));
});

$(document).on("click", "#trading-inquiry-today", function () {
    fetchData('2', "today", $("#flot-placeholder-trading-inquiry"));
});
$(document).on("click", "#trading-inquiry-yesterday", function () {
    fetchData('2', "yesterday", $("#flot-placeholder-trading-inquiry"));
});
$(document).on("click", "#trading-inquiry-week", function () {
    fetchData('2', "week", $("#flot-placeholder-trading-inquiry"));
});
$(document).on("click", "#trading-inquiry-month", function () {
    fetchData('2', "month", $("#flot-placeholder-trading-inquiry"));

});

$(document).on("click", "#inquiry-today", function () {
    fetchData('3', "today", $("#flot-placeholder-inquiry"));
});
$(document).on("click", "#inquiry-yesterday", function () {
    fetchData('3', "yesterday", $("#flot-placeholder-inquiry"));
});
$(document).on("click", "#inquiry-week", function () {
    fetchData('3', "week", $("#flot-placeholder-inquiry"));
});
$(document).on("click", "#inquiry-month", function () {
    fetchData('3', "month", $("#flot-placeholder-inquiry"));
});

$(document).on("click", "#finance-today", function () {
    fetchData('4', "today", $("#flot-placeholder-finance"));
});
$(document).on("click", "#finance-yesterday", function () {
    fetchData('4', "yesterday", $("#flot-placeholder-finance"));
});
$(document).on("click", "#finance-week", function () {
    fetchData('4', "week", $("#flot-placeholder-finance"));
});
$(document).on("click", "#finance-month", function () {
    fetchData('4', "month", $("#flot-placeholder-finance"));
});

$(document).on("click", "#auth-today", function () {
    fetchData('5', "today", $("#flot-placeholder-auth"));
});
$(document).on("click", "#auth-yesterday", function () {
    fetchData('5', "yesterday", $("#flot-placeholder-auth"));
});
$(document).on("click", "#auth-week", function () {
    fetchData('5', "week", $("#flot-placeholder-auth"));
});
$(document).on("click", "#auth-month", function () {
    fetchData('5', "month", $("#flot-placeholder-auth"));
});

$(document).on("click", "#system-today", function () {
    fetchData('6', "today", $("#flot-placeholder-system"));
});
$(document).on("click", "#system-yesterday", function () {
    fetchData('6', "yesterday", $("#flot-placeholder-system"));
});
$(document).on("click", "#system-week", function () {
    fetchData('6', "week", $("#flot-placeholder-system"));
});
$(document).on("click", "#system-month", function () {
    fetchData('6', "month", $("#flot-placeholder-system"));
});

$(document).on("click", "#cust-inquiry-today", function () {
    fetchData('10', "today", $("#flot-placeholder-cust-inquiry"));
});
$(document).on("click", "#cust-inquiry-yesterday", function () {
    fetchData('10', "yesterday", $("#flot-placeholder-cust-inquiry"));
});
$(document).on("click", "#cust-inquiry-week", function () {
    fetchData('10', "week", $("#flot-placeholder-cust-inquiry"));
});
$(document).on("click", "#cust-inquiry-month", function () {
    fetchData('10', "month", $("#flot-placeholder-cust-inquiry"));
});

$(document).on("click", "#subscription-today", function () {
    fetchData('11', "today", $("#flot-placeholder-subscription"));
});
$(document).on("click", "#subscription-yesterday", function () {
    fetchData('11', "yesterday", $("#flot-placeholder-subscription"));
});
$(document).on("click", "#subscription-week", function () {
    fetchData('11', "week", $("#flot-placeholder-subscription"));
});
$(document).on("click", "#subscription-month", function () {
    fetchData('11', "month", $("#flot-placeholder-subscription"));
});

function fetchData(groupNumber, duration, graph) {
    graph.hide();
    $('#loadingDiv').show();
    $.get('data/count', {group: groupNumber, date: duration}, function (responseJson) {          // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
        graph.show();
        $('#loadingDiv').hide();
        var graphHeight = 20 * responseJson.data.length;
        if (graphHeight < 200) {
            graphHeight = 200;
        }
        graph.css("height", graphHeight);
        refreshGraph(responseJson, graph)
        refreshTime();
    });
}

function refreshTime() {
    $.get("data/serverTime", function (response) {
        $("#server-time-text").html(response[0] + "<br/>" + response[1]);
    });
    setTimeout(refreshTime, 60000);
}


function refreshGraph(response, graph) {
    options.yaxis.ticks = response.legend;
    $.plot(graph, [response], options);
    graph.UseTooltip();

}

var previousPoint = null, previousLabel = null;

$.fn.UseTooltip = function () {
    $(this).bind("plothover", function (event, pos, item) {
        if (item) {
            if ((previousLabel != item.series.label) ||
                    (previousPoint != item.dataIndex)) {
                previousPoint = item.dataIndex;
                previousLabel = item.series.label;
                $("#tooltip").remove();

                var x = item.datapoint[0];
                var y = item.datapoint[1];

                var color = item.series.color;

                showTooltip(item.pageX,
                        item.pageY,
                        color,
                                item.series.yaxis.ticks[y].label + " : " + x);
            }
        } else {
            $("#tooltip").remove();
            previousPoint = null;
        }
    });
};

function showTooltip(x, y, color, contents) {
    $('<div id="tooltip">' + contents + '</div>').css({
        position: 'absolute',
        display: 'none',
        top: y - 10,
        left: x + 10,
        border: '2px solid ' + color,
        padding: '3px',
        'font-size': '9px',
        'z-index': 9999,
        'border-radius': '5px',
        'background-color': '#fff',
        'font-family': 'Verdana, Arial, Helvetica, Tahoma, sans-serif',
        opacity: 0.9
    }).appendTo("body").fadeIn(200);
}
</script>
</head>
<body>

<%@include file="menu.jsp" %>

<div class="content" id="container">
    <h2>Request Type Statistics</h2>

    <div id="tabs" style="width:100%;">
        <ul>
            <li id="trade"><a id="tabs-a1" href="#tabs-1">Trade</a></li>
            <li id="trad-inquiry"><a id="tabs-a2" href="#tabs-2">Trading Inquiry</a></li>
            <li id="inquiry"><a id="tabs-a3" href="#tabs-3">Inquiry</a></li>
            <li id="finance"><a id="tabs-a4" href="#tabs-4">Finance & Holdings</a></li>
            <li id="auth"><a id="tabs-a5" href="#tabs-5">Authentication</a></li>
            <li id="system"><a id="tabs-a6" href="#tabs-6">System</a></li>
            <li id="cust-inquiry"><a id="tabs-a7" href="#tabs-7">Customer Inquiry</a></li>
            <li id="subscription"><a id="tabs-a8" href="#tabs-8">Subscription</a></li>
        </ul>

        <%--<div id="tab_container" style="height: 100%">--%>
        <div id="tabs-1" style="width:800px;">
            <div id="radio-trade">
                <input type="radio" id="trade-today" name="radio-trade" checked="checked" value="today"><label
                    for="trade-today">Today</label>
                <input type="radio" id="trade-yesterday" name="radio-trade" value="yesterday"> <label
                    for="trade-yesterday">Yesterday</label>
                <input type="radio" id="trade-week" name="radio-trade" value="week"> <label for="trade-week">This
                week</label>
                <input type="radio" id="trade-month" name="radio-trade" value="month"><label for="trade-month">This
                month</label>
            </div>
            <div id="flot-placeholder-trade" style="height:600px;text-align:center;margin:10px"></div>
        </div>
        <div id="tabs-2" style="width:800px;">
            <div id="radio-trading-inquiry">
                <input type="radio" id="trading-inquiry-today" name="radio-trading-inquiry" value="today"
                       checked="checked"><label
                    for="trading-inquiry-today">Today</label>
                <input type="radio" id="trading-inquiry-yesterday" name="radio-trading-inquiry" value="yesterday">
                <label for="trading-inquiry-yesterday">Yesterday</label>
                <input type="radio" id="trading-inquiry-week" name="radio-trading-inquiry" value="week"><label
                    for="trading-inquiry-week">This week</label>
                <input type="radio" id="trading-inquiry-month" name="radio-trading-inquiry" value="month"><label
                    for="trading-inquiry-month">This month</label>
            </div>
            <div id="flot-placeholder-trading-inquiry" style="height:600px;text-align:center;margin:10px"></div>
        </div>
        <div id="tabs-3" style="width:800px;">
            <div id="radio-inquiry">
                <input type="radio" id="inquiry-today" name="radio-inquiry" value="today" checked="checked"><label
                    for="inquiry-today">Today</label>
                <input type="radio" id="inquiry-yesterday" name="radio-inquiry" value="yesterday"> <label
                    for="inquiry-yesterday">Yesterday</label>
                <input type="radio" id="inquiry-week" name="radio-inquiry" value="week"><label for="inquiry-week">This
                week</label>
                <input type="radio" id="inquiry-month" name="radio-inquiry" value="month"><label for="inquiry-month">This
                month</label>
            </div>
            <div id="flot-placeholder-inquiry" style="height:600px;text-align:center;margin:10px"></div>

        </div>
        <div id="tabs-4" style="width:800px;">
            <div id="radio-finance">
                <input type="radio" id="finance-today" name="radio-finance" value="today" checked="checked"><label
                    for="finance-today">Today</label>
                <input type="radio" id="finance-yesterday" name="radio-finance" value="yesterday"> <label
                    for="finance-yesterday">Yesterday</label>
                <input type="radio" id="finance-week" name="radio-finance" value="week"><label for="finance-week">This
                week</label>
                <input type="radio" id="finance-month" name="radio-finance" value="month"><label for="finance-month">This
                month</label>
            </div>
            <div id="flot-placeholder-finance" style="height:600px;text-align:center;margin:10px"></div>

        </div>
        <div id="tabs-5" style="width:800px;">
            <div id="radio-auth">
                <input type="radio" id="auth-today" name="radio-auth" value="today" checked="checked"><label
                    for="auth-today">Today</label>
                <input type="radio" id="auth-yesterday" name="radio-auth" value="yesterday"> <label
                    for="auth-yesterday">Yesterday</label>
                <input type="radio" id="auth-week" name="radio-auth" value="week"><label for="auth-week">This
                week</label>
                <input type="radio" id="auth-month" name="radio-auth" value="month"><label for="auth-month">This
                month</label>
            </div>
            <div id="flot-placeholder-auth" style="height:600px;text-align:center;margin:10px"></div>

        </div>
        <div id="tabs-6" style="width:800px;">
            <div id="radio-system">
                <input type="radio" id="system-today" name="radio-system" value="today" checked="checked"><label
                    for="system-today">Today</label>
                <input type="radio" id="system-yesterday" name="radio-system" value="yesterday"> <label
                    for="system-yesterday">Yesterday</label>
                <input type="radio" id="system-week" name="radio-system" value="week"><label for="system-week">This
                week</label>
                <input type="radio" id="system-month" name="radio-system" value="month"><label for="system-month">This
                month</label>
            </div>
            <div id="flot-placeholder-system" style="height:600px;text-align:center;margin:10px"></div>

        </div>
        <div id="tabs-7" style="width:800px;">
            <div id="radio-cust-inquiry">
                <input type="radio" id="cust-inquiry-today" name="radio-cust-inquiry" value="today"
                       checked="checked"><label
                    for="cust-inquiry-today">Today</label>
                <input type="radio" id="cust-inquiry-yesterday" name="radio-cust-inquiry" value="yesterday"> <label
                    for="cust-inquiry-yesterday">Yesterday</label>
                <input type="radio" id="cust-inquiry-week" name="radio-cust-inquiry" value="week"><label
                    for="cust-inquiry-week">This
                week</label>
                <input type="radio" id="cust-inquiry-month" name="radio-cust-inquiry" value="month"><label
                    for="cust-inquiry-month">This
                month</label>
            </div>
            <div id="flot-placeholder-cust-inquiry" style="height:600px;text-align:center;margin:10px"></div>

        </div>
        <div id="tabs-8" style="width:800px;">
            <div id="radio-subscription">
                <input type="radio" id="subscription-today" name="radio-subscription" value="today"
                       checked="checked"><label
                    for="subscription-today">Today</label>
                <input type="radio" id="subscription-yesterday" name="radio-subscription" value="yesterday"> <label
                    for="subscription-yesterday">Yesterday</label>
                <input type="radio" id="subscription-week" name="radio-subscription" value="week"><label
                    for="subscription-week">This
                week</label>
                <input type="radio" id="subscription-month" name="radio-subscription" value="month"><label
                    for="subscription-month">This
                month</label>
            </div>
            <div id="flot-placeholder-subscription" style="height:600px;text-align:center;margin:10px"></div>

            <%--</div>--%>
        </div>
    </div>

    <div id="loadingDiv" style=" position: fixed; top: 50%; left: 50%;
    margin-left: -10px; margin-top: -10px;">
        <img src="${pageContext.request.contextPath}/resources/images/ajax-loader.gif" alt="Loading..."/>
    </div>

    <%--<input type="text" id="displayDate"/><br/>--%>
    <%--<input type="text" id="messageDate" name="messageDate"/>--%>
    <%--readonly="readonly--%>
    <%--<input type="button" id="refreshData" value="Submit"/>--%>
    <%--<br/>--%>
    <%--<div style="width:800px;height:600px;text-align:center;margin:10px">--%>
    <%--<div id="flot-placeholder" style="width:100%;height:100%;"></div>--%>
    <%--</div>--%>
</div>
</body>
</html>