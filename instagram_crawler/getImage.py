import urllib.request
import json

def DownloadSingleFile(fileURL, cnt):
    print('Downloading image...')
    fileName = './output/img' + str("%06d"%cnt) + '.jpg'
    urllib.request.urlretrieve(fileURL, fileName)
    print('Done. ' + fileName)

if __name__ == '__main__':
    with open('output.json') as data_file:
        data = json.load(data_file)

    for i in range(0, len(data)):
        if i > 3487:
            instagramURL = data[i]['img_url']
            # print(instagramURL)
            DownloadSingleFile(instagramURL, i)

