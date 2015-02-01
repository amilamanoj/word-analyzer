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

    <script type="text/javascript"
            src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.min.js"></script>
    <script type="text/javascript"
            src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.barnumbers.js"></script>
    <script language="javascript" src="${pageContext.request.contextPath}/resources/js/flot/jquery.flot.navigate.js"
            type="text/javascript"></script>
    <link href="${pageContext.request.contextPath}/resources/css/trs_console.css" rel="stylesheet" type="text/css"/>
    <link href="${pageContext.request.contextPath}/resources/css/jquery-ui-1.8.4.custom.css" rel="stylesheet"
          type="text/css" media="all"/>
    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-ui.min.js"></script>

    <script src="${pageContext.request.contextPath}/resources/js/jquery.jpanelmenu.min.js"
            type="text/javascript"></script>

    <script type="text/javascript">
        $(document).ready(function () {
            var jPM = $.jPanelMenu({
                menu: 'header.main nav',
                animated: true
            });
            jPM.on();
//        jPM.open();
            $('#loadingDiv').hide();

            $("<div id='tooltip'></div>").css({
                position: "absolute",
                zIndex: 9999,
                display: "none",
                border: "1px solid #fdd",
                padding: "2px",
                "background-color": "#fee",
                opacity: 0.80
            }).appendTo("body");

            $("#flot-placeholder").bind("plothover", function (event, pos, item) {

                if (item) {
                    var x = item.datapoint[0],
                            y = item.datapoint[1];

                    $("#tooltip").html("Time: " + labels[x][1])
                            .css({top: item.pageY + 5, left: item.pageX + 5})
                            .fadeIn(200);
                } else {
                    $("#tooltip").hide();
                }
            });

            fetchData("today", $("#flot-placeholder"));
            refreshTime();
        });


        $(function () {
            $("#tabs").tabs({heightStyle: "fill"}).addClass("ui-tabs-vertical ui-helper-clearfix");
            $("#tabs li").removeClass("ui-corner-top").addClass("ui-corner-left");
        });
        $(function () {
            $("#radio-refresh").buttonset();
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
                lines: {
                    show: true,
                    fill: false
                },
                points: {
                    show: false
                }
            },
            grid: {
                hoverable: true,
//                minBorderMargin: 20,
//                labelMargin: 10,
                backgroundColor: { colors: ["#171717", "#4F4F4F"] },
                margin: {
                }
            },
            xaxis: {
//                rotateTicks: 135
                tickFormatter: function (val, axis) {
                    var label = labels[val];
                    if (label) {
                        return label[1];
                    } else {
                        return "";
                    }
                }
            },
            yaxis: {
                min: 0,
                tickDecimals: 0,
                zoomRange: false,
                panRange: false
            },
            legend: {
                show: true,
                position: "nw"
            },
            zoom: {
                interactive: true
            },
            pan: {
                interactive: true
            }
        };

        $(document).on("click", "#refresh-today", function () {
            fetchData("today", $("#flot-placeholder"));
        });
        $(document).on("click", "#refresh-yesterday", function () {
            fetchData("yesterday", $("#flot-placeholder"));
        });
        $(document).on("click", "#refresh-week", function () {
            fetchData("week", $("#flot-placeholder"));
        });
        $(document).on("click", "#refresh-month", function () {
            fetchData("month", $("#flot-placeholder"));
        });

        function refreshTime() {
            $.get("data/serverTime", function (response) {
                $("#server-time-text").html(response[0] + "<br/>" + response[1]);
            });
            setTimeout(refreshTime, 60000);
        }

        var labels;

        function fetchData(duration, graph) {
            graph.hide();
            $('#loadingDiv').show();
            $.get('data/clientCount', {date: duration}, function (responseJson) {          // Execute Ajax GET request on URL of "someservlet" and execute the following function with Ajax response JSON...
                graph.show();
                $('#loadingDiv').hide();
                refreshGraph(responseJson, graph)
            });
        }

        function refreshGraph(response, graph) {
            labels = response[0].legend;
            $.plot(graph, response, options);

        }


    </script>
</head>
<body>

<%@include file="menu.jsp" %>

<div class="content" id="container">
    <h2>Client Count</h2>

    <div id="radio-refresh">
        <input type="radio" id="refresh-today" name="radio-refresh" checked="checked" value="today"><label
            for="refresh-today">Today</label>
        <input type="radio" id="refresh-yesterday" name="radio-refresh" value="yesterday"><label
            for="refresh-yesterday">
        Yesterday</label>
        <input type="radio" id="refresh-week" name="radio-refresh" value="week"> <label for="refresh-week">This
        week</label>
        <input type="radio" id="refresh-month" name="radio-refresh" value="month"><label for="refresh-month">This
        month</label>
    </div>
    <div id="graph-canvas" style="width:1000px;height:500px;text-align:center;">
        <div id="flot-placeholder" style="width:100%;height:100%;"></div>
    </div>

    <div id="loadingDiv" style=" position: fixed; top: 50%; left: 50%;
    margin-left: -10px; margin-top: -10px;">
        <img src="${pageContext.request.contextPath}/resources/images/ajax-loader.gif" alt="Loading..."/>
    </div>

</div>
</body>
</html>