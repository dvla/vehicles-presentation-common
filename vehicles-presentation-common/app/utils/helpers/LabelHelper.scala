package utils.helpers

object LabelHelper {

  // Display a label above the field.
  // If '_label key is in the args list then return the label
  // else return the field's id
  def label(id: String, args: Map[Symbol, Any]): String = {
    if (args.contains('_label)) args('_label).toString
    else id
  }
}