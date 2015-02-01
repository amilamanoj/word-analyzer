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
            $("#performGC").button();
            refreshHeap($("#flot-placeholder-heap"));
            refreshGC();
            refreshTime();
        });


        $(document).on("click", "#performGC", function () {
            $.get('data/performGC', function (response) {
            });
        });

        var options = {
            points: { show: true},
            lines: { show: true, fill: true },
            grid: {
                hoverable: true,
                borderWidth: 1,
                minBorderMargin: 20,
                labelMargin: 10,
                backgroundColor: {
                    colors: ["#fff", "#e4f4f4"]
                },
                margin: {
                    top: 8,
                    bottom: 20,
                    left: 20
                }
            },
            xaxis: {
                tickFormatter: function (x) {
                    var date = new Date(x);
                    return date.getHours() + ':' + date.getMinutes() + ':' + date.getSeconds();
                }
            },
            yaxis: {
            },
            legend: {
                show: true
            }
        };

        function refreshTime() {
            $.get("data/serverTime", function (response) {
                $("#server-time-text").html(response[0] + "<br/>" + response[1]);
            });
            setTimeout(refreshTime, 60000);
        }

        function refreshHeap(graph) {
            $.get('data/heapUsed', function (response) {

                $("#usedHeap").html(response[0] + "MB");
                $("#currentHeap").html(response[1] + "MB");
                $("#freeHeap").html(response[2] + "MB");
                $("#MaxHeap").html(response[3] + "MB");

                if (usedHeapDataSet.length == 20) {
                    usedHeapDataSet.shift();
                }
                if (totalHeap.length == 20) {
                    totalHeap.shift();
                }
                var time = new Date();
                usedHeapDataSet.push([time, response[0]]);
                totalHeap.push([time, response[1]]);

                var data = [
                    {data: totalHeap, id: "TotalHeap", label: 'Current Heap', lines: {show: true}, points: {show: true}},
                    {data: usedHeapDataSet, id: "UsedHeap", label: 'Used Heap', lines: {show: true}, points: {show: true}}
                ]
                refreshGraph(data, graph)
                setTimeout(function () {
                    refreshHeap(graph);
                }, 1000);
            });
        }

        function refreshGC() {
            $.get('data/lastGcInfo', function (response) {

                $("#gcTime").html(response[0]);
                $("#gcDuration").html(response[1]);
                $("#gcId").html(response[2]);
                $("#gcReason").html(response[3]);

                setTimeout(function () {
                    refreshGC();
                }, 1000);
            });
        }

        function refreshGraph(data, graph) {
            $.plot(graph, data, options);
        }

        var usedHeapDataSet = [];
        var totalHeap = [];

    </script>
</head>
<body>

<%@include file="menu.jsp" %>

<div class="content" id="container">
    <br/>

    <table class="jtable" style="border: none; margin-left: 20px">
        <tr>
            <td style="min-width: 300px"><h3>JVM Heap Usage</h3></td>
            <td><h3>Last Garbage Collection</h3></td>
        </tr>
        <tr>
            <td>
                <table class="jtable" style="border: none">

                    <tr>
                        <td style="min-width: 150px">Used heap:</td>
                        <td>
                            <div id="usedHeap"></div>
                        </td>
                    </tr>
                    <tr>
                        <td>Current heap:</td>
                        <td>
                            <div id="currentHeap"></div>
                        </td>
                    </tr>
                    <tr>
                        <td>Free heap:</td>
                        <td>
                            <div id="freeHeap"></div>
                        </td>
                    </tr>
                    <tr>
                        <td>Max available heap:</td>
                        <td>
                            <div id="MaxHeap"></div>
                        </td>
                    </tr>
                    <tr>
                        <td>Perform GC:</td>
                        <td>
                            <label id="performGC">Perform GC</label>
                        </td>
                    </tr>
                </table>
            </td>
            <td style="vertical-align: top">
                <table class="jtable" style="border: none">

                    <tr>
                        <td style="width: 100px">Time:</td>
                        <td>
                            <div id="gcTime"></div>
                        </td>
                    </tr>
                    <tr>
                        <td>Duration:</td>
                        <td>
                            <div id="gcDuration"></div>
                        </td>
                    </tr>
                    <tr>
                        <td>Information:</td>
                        <td>
                            <div id="gcId"></div>
                        </td>
                    </tr>
                    <tr>
                        <td>Reason:</td>
                        <td>
                            <div id="gcReason"></div>
                        </td>
                    </tr>
                </table>

            </td>
        </tr>
    </table>


    <br/>

    <div style="height:300px;text-align:center;margin-right:50px">
        <div id="flot-placeholder-heap" style="width:100%;height:100%;"></div>
    </div>

</div>
</body>
</html>