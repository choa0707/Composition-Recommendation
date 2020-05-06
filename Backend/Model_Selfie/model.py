from keras.models import model_from_json
import sys
import math
import numpy as np
def warn(*args,**kwargs):
    pass
import warnings
warnings.warn = warn
input_array = [sys.argv[1], sys.argv[2], sys.argv[3], sys.argv[4]]
json_file  = open("./learning/model.json", "r")
loaded_model_json = json_file.read()
json_file.close()
loaded_model = model_from_json(loaded_model_json)

loaded_model.load_weights("./learning/model.h5")

loaded_model.compile(loss="categorical_crossentropy", optimizer="rmsprop", metrics = ['accuracy'])


np_array = np.array([input_array])

result = loaded_model.predict_on_batch(np_array)
recover = float(np.argmax(result,axis=1).reshape(-1,1))
print("result",recover)

