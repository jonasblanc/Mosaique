# Mosaique
Mosaique is a tool to transform an given image into a mosaique constitued of smaller images. There is still room for improvement in color comparaison. 

## Technologies
You will need java and [Jsoup](https://jsoup.org) to be able to fully run this project. (Jsoup is needed to download images from google image as explained below).

## Usage
Clone this repo:
```
$ git clone https://github.com/jonasblanc/Mosaique.git
```
Provide the image you want to transform into a mosaique in [images/input/big](images/input/big).

Some settings are available for you to play with in [Main](src/Main.java). You can specify a "SEARCH_TERM" that will be used to download images from google image if "DO_YOU_PROVIDE_IMAGES" is set to "false", otherwise you have to provide images in [images/input/smalls](images/input/smalls). In both cases, remember that the larger your library of input images and the more diverse the colors, the better the result.

There are four methods of color comparison to choose from. For each considered pixel...
  * RGB selects the image with the closest mean based on RGB representation.
  * RGB_RDM same as RGB butif there is a tie, picks one at random, while RGB chooses the first one.
  * LAB selects the image with the closest mean based on CIE-LAB representation. (The results seems to indicate that I should spend more time on this one to correct it.)
  * MONOCHROME selects the image with the closest mean after transforming it into black and white image.
