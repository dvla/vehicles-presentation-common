package utils.helpers

object SelectHelper {

  def defaultOption(htmlArgs: Map[Symbol, Any]) = htmlArgs.get('_default).map { defaultValue =>
    <option class="blank" value="">@defaultValue</option>
  }
}