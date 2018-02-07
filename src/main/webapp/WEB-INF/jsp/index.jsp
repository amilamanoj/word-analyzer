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
            $("#refreshLog").button();
            $("#loadingImage").hide();
//            refreshConnectorStatus();
        });

        $(document).on("click", "#uploadFile", function () {
//            refreshConnectorStatus();
        });

        //        function refreshConnectorStatus() {
        //
        //            $.get("data/cometStatus", function (response) {
        //                updateStatus($("#comet_stat"), response[0]);
        //                $("#comet_time").html(response[1]);
        //            });
        //            setTimeout(refreshConnectorStatus, 20000);
        //
        //        }


        //        function checkSession(data) {
        //            var exp = new RegExp("j_security_check");
        //            if (exp.test(data)) {
        //                window.location.replace("/console");
        //            }
        //        }

    </script>


</head>
<body>

<%@include file="menu.jsp" %>

<div class="content" id="container">

    <h2>Overview</h2>
    <label id="refreshLog">Refresh</label> <br/><br/>

    <h3>Upload your book...</h3>

    <hr/>

    <div id="connectivity">

        <form method="POST" enctype="multipart/form-data"
              action="upload">
            File to upload: <input type="file" name="file"><br/>
            <%--Name: <input--%>
            <%--type="text" name="name"><br/> <br/>--%>
            <input type="HIDDEN" name="name" value="myFile"/>
            <input type="submit" value="Upload"> Press here to upload the file!
        </form>
    </div>
</div>

</body>
</html>