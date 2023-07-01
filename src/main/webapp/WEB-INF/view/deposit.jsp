<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Deposit</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
                <%
                // Clear browser cache
           		response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
           		response.setHeader("Pragma", "no-cache"); // HTTP 1.0.
           		response.setHeader("Expires", "0"); // Proxies.

           		if (session.getAttribute("user")  == null ){
           		%>
           		<c:redirect url = "/login"/>
           		<%
           		}
           		%>
<div class="container">
    <jsp:include page="headerUser.jsp"></jsp:include>
    <h3 class="text-center">Create Deposit</h3>
    <form id="createDepositForm">
        <div class="form-group">
            <label for="depositType">Deposit Type:</label>
            <select class="form-control" id="depositType" name="depositType">
                <option value="fixed">Fixed Deposit</option>
                <option value="recurring">Recurring Deposit</option>
            </select>
        </div>
        <div id="fixedDepositSection" style="display: none;">
            <h5>Fixed Deposit:</h5>
            <div class="form-group">
                <label for="fixedDepositRate">Rate:</label>
                <input type="text" class="form-control" id="fixedDepositRate" name="fixedDepositRate" placeholder="Enter Rate">
            </div>
            <div class="form-group">
                <label for="fixedDepositPrincipal">Principal:</label>
                <input type="text" class="form-control" id="fixedDepositPrincipal" name="fixedDepositPrincipal" placeholder="Enter Principal">
            </div>
            <div class="form-group">
                <label for="fixedDepositYears">Years:</label>
                <input type="text" class="form-control" id="fixedDepositYears" name="fixedDepositYears" placeholder="Enter Years">
            </div>
        </div>
        <div id="recurringDepositSection" style="display: none;">
            <h5>Recurring Deposit:</h5>
            <div class="form-group">
                <label for="recurringDepositRate">Rate:</label>
                <input type="text" class="form-control" id="recurringDepositRate" name="recurringDepositRate" placeholder="Enter Rate">
            </div>
            <div class="form-group">
                <label for="recurringDepositPrincipal">Principal:</label>
                <input type="text" class="form-control" id="recurringDepositPrincipal" name="recurringDepositPrincipal" placeholder="Enter Principal">
            </div>
            <div class="form-group">
                <label for="recurringDepositMonths">Months:</label>
                <input type="text" class="form-control" id="recurringDepositMonths" name="recurringDepositMonths" placeholder="Enter Months">
            </div>
        </div>
        <button type="submit" class="btn btn-primary">Create</button>
    </form>
</div>

<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
<script>
    $(document).ready(function() {
        $('#depositType').change(function() {
            var depositType = $(this).val();
            if (depositType === 'fixed') {
                $('#fixedDepositSection').show();
                $('#recurringDepositSection').hide();
            } else if (depositType === 'recurring') {
                $('#fixedDepositSection').hide();
                $('#recurringDepositSection').show();
            } else {
                $('#fixedDepositSection').hide();
                $('#recurringDepositSection').hide();
            }
        });

        $('#createDepositForm').submit(function(event) {
            event.preventDefault();

            var depositType = $('#depositType').val();
            var rate = null;
            var principal = null;
            var years = null;
            var months = null;

            if (depositType === 'fixed') {
                rate = $('#fixedDepositRate').val();
                principal = $('#fixedDepositPrincipal').val();
                years = $('#fixedDepositYears').val();
            } else if (depositType === 'recurring') {
                rate = $('#recurringDepositRate').val();
                principal = $('#recurringDepositPrincipal').val();
                months = $('#recurringDepositMonths').val();
            }

            var depositRequest = {
                depositType: depositType,
                rate: rate,
                principal: principal,
                years: years,
                months: months
            };

            $.ajax({
                type: 'POST',
                url: '/create',
                contentType: 'application/json',
                data: JSON.stringify(depositRequest),
                success: function(response) {
                    alert('Deposit created successfully.');
                    location.reload(); // Refresh the page
                },
                error: function(xhr, status, error) {
                    alert('Error creating deposit: ' + error);
                }
            });
        });
    });
</script>
</body>
</html>
