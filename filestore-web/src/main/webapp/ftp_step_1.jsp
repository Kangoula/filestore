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
	<style>
		.img-wrapper{
			position: absolute;
			left: 20em;
			width: 20em;
		}

		.img-wrapper img{
			width: 100%;
			height: 100%;
		}

	</style>
</head>
<body>
	<div class="container-fluid">
		<div class="page-header">
			<h1>
				Step 2:
			</h1>
			<p>Please copy the token below and use it as a username when connecting with your ftp client :</p>
			<div class="img-wrapper">
				<a href="./img/example.gif" target="_blank">
					<img src="./img/example.gif" alt="">
				</a>
			</div>
			<p>---------------------------------</p>
			<p><%= request.getParameter("id") %></p>
			<p>---------------------------------</p>
			<p></p>
			<p></p>
			<p></p>
			<h2>Once you are done</h2>
			<p>If you are done uploading your file, click on this button:</p>
			<a href="./api/files/complete/<%= request.getParameter("id") %>">
				<button>I have finished my upload &rarr;</button>
			</a>
			<small><i>Note: be sure that you have uploaded your file using an ftp client</i></small>
		</div>



	</div>
	<script
		src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
	<script
		src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"
		integrity="sha512-K1qjQ+NcF2TYO/eI3M6v8EiNYZfA95pQumfvcVrTHtwQVDG+aHRqLi/ETn2uB+1JqwYqVG3LIvdm9lj6imS/pQ=="
		crossorigin="anonymous"></script>
</body>
</html>