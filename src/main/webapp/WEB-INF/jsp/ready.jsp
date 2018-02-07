<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<!--@author AmilaS-->
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <meta http-equiv="content-type" content="text/html; charset=utf-8"/>
    <title>Word Analyzer</title>

    <link href="${pageContext.request.contextPath}/resources/css/jquery-ui-1.8.4.custom.css" rel="stylesheet"
          type="text/css" media="all"/>
    <link href="${pageContext.request.contextPath}/resources/css/trs_console.css" rel="stylesheet" type="text/css"/>

    <script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/jquery-1.10.2.min.js"></script>
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
            jPM.open();
            $("#analyzeButton").button();
            $("#loadingImage").hide();
        });

        $(document).on("click", "#analyzeButton", function () {
            $.get("analyze", function (response) {
                $("#status").html(response);
            });
            updateProgress();
        });

        function updateProgress() {
            var sCode = 0;
            $.get("progress", function (response) {
                $("#status").html(response.status);
                $("#count").html(response.progress);
                sCode = response.statusCode;
            });
//            if (sCode.statusCode == 1 ) {
            setTimeout(updateProgress, 500);
//            }
        }


    </script>


</head>
<body>

<%@include file="menu.jsp" %>

<div class="content" id="container">

    <h2>Word Analyzer</h2>

    <h3>Ready to analyze...</h3>

    <hr/>

    <div id="jobInfo">
        <table class="jtable" style="border: none">
            <tr>
                <td style="width: 100px">Title</td>
                <td>
                    <div id="title">${pageScope.title}</div>
                </td>
            </tr>
            <tr>
                <td>Analyze</td>
                <td>
                    <label id="analyzeButton">Analyze</label>
                </td>
            </tr>
            <tr>
                <td>Status</td>
                <td>
                    <label id="status"></label>
                </td>
            </tr>
            <tr>
                <td>Status</td>
                <td>
                    <label id="count"></label>
                </td>
            </tr>
        </table>
    </div>
</div>

</body>
</html>