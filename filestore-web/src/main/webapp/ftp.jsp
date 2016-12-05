<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>FileStore Index page</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css"
	integrity="sha512-dTfge/zgoMYpP7QbHy4gWMEGsbsdZeCXz7irItjcC3sPUFtf0kuFbDz/ixG7ArTxmDjLXDmezHubeNikyKGVyQ=="
	crossorigin="anonymous">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css"
	integrity="sha384-aUGj/X2zp5rLCbBxumKTCw2Z50WgIr1vs/PFN4praOTvYXWlVyh2UtNUU0KAUhAX"
	crossorigin="anonymous">
</head>
<body>
	<div class="container-fluid">
		<div class="page-header">
			<h1>
				Send a new file via FTP
			</h1>
			<p>You can also <a href="/">send a file with your browser</a>.</p>
			<p>First, please enter the details about the file you are going to upload.</p>
			<p>You will get a token that will allow you to upload your file with any FTP client.</p>
		</div>
		<form class="form-horizontal" action="./api/files/prepare" method="post"
			enctype="multipart/form-data">
			<div class="form-group">
				<label for="owner" class="col-sm-2 control-label">Your email</label>
				<div class="col-sm-10">
					<input type="email" class="form-control" id="owner" name="owner"
						placeholder="your email adress...">
				</div>
			</div>
			<div class="form-group">
				<label for="receivers" class="col-sm-2 control-label">Receiver
					email</label>
				<div class="col-sm-10">
					<input type="email" class="form-control" id="receivers"
						name="receivers" placeholder="receiver email adress...">
				</div>
			</div>
			<div class="form-group">
				<label for="message" class="col-sm-2 control-label">Message</label>
				<div class="col-sm-10">
					<textarea class="form-control" rows="3" id="message" name="message"></textarea>
				</div>
			</div>
			<div class="form-group">
				<div class="col-sm-offset-2 col-sm-10">
					<button type="submit" class="btn btn-default">Get my upload token</button>
				</div>
			</div>
		</form>
	</div>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"
		integrity="sha512-K1qjQ+NcF2TYO/eI3M6v8EiNYZfA95pQumfvcVrTHtwQVDG+aHRqLi/ETn2uB+1JqwYqVG3LIvdm9lj6imS/pQ=="
		crossorigin="anonymous"></script>
</body>
</html>