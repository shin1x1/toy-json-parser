package parser

type ValueType = int

const (
	_ ValueType = iota
	ValueTypeTrue
	ValueTypeFalse
	ValueTypeNull
	ValueTypeNumber
	ValueTypeString
	ValueTypeArray
	ValueTypeObject
)

type JsonValue struct {
	valueType   ValueType
	stringValue string
	numberValue float64
	arrayValue  []*JsonValue
	objectValue map[string]*JsonValue
}

func NewJsonValue(valueType ValueType) *JsonValue {
	return &JsonValue{valueType: valueType}
}

func NewJsonValueString(value string) *JsonValue {
	return &JsonValue{valueType: ValueTypeString, stringValue: value}
}

func NewJsonValueNumber(value float64) *JsonValue {
	return &JsonValue{valueType: ValueTypeNumber, numberValue: value}
}

func NewJsonValueArray(value []*JsonValue) *JsonValue {
	return &JsonValue{valueType: ValueTypeArray, arrayValue: value}
}

func NewJsonValueObject(value map[string]*JsonValue) *JsonValue {
	return &JsonValue{valueType: ValueTypeObject, objectValue: value}
}
