# Google Vision API

In this tutorial we will look at Google Cloud Vision API. We will cover face detection and OCR – Optical Character recognition along with many other things. We don’t actually need Fresco and Glide Libraries for this project but for some reason Dexter is throwing errors without these libraries. I still couldn’t figure out why, so for the time being just add these 2 dependencies as well. If it works fine for you without them then go ahead and remove them.

In short this is all about extracting features from an image and passing them through certain filters like specifying the type of search. Ex: Safe Search to see if the image is kid friendly, Face recognition to see if the image contains a face and what its features are, OCR for checking if the image contains any characters or words etc. After setting the filters we send that particular data to the Google servers through an API call. The call gives us a response which we parse and show it as a string.


